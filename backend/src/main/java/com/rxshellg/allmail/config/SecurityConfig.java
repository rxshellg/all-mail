package com.rxshellg.allmail.config;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.service.AppUserService;
import com.rxshellg.allmail.service.ConnectedAccountService;
import com.rxshellg.allmail.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Central security setup
 *
 * Handles Google OAuth login, distinguishes normal login from additional
 * accounts, stores connected account tokens, and allows the React frontend
 * to call protected backend endpoints during local development.
 */
@Configuration
public class SecurityConfig {

    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final ConnectedAccountService connectedAccountService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityConfig(
            AppUserService appUserService,
            AppUserRepository appUserRepository,
            ConnectedAccountService connectedAccountService,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.connectedAccountService = connectedAccountService;
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * Builds the main Spring Security rules for the backend:
     * public OAuth routes, protected API routes, Google login handling, and logout.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2AuthorizationRequestResolver authorizationRequestResolver
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            HttpSession session = request.getSession();

                            Long connectOwnerUserId =
                                    (Long) session.getAttribute(SessionKeys.CONNECT_OWNER_USER_ID);
                            
                            // Add-account flow: attach the new Gmail mailbox to the existing AllMail user
                            if (connectOwnerUserId != null) {
                                AppUser owner = appUserRepository.findById(connectOwnerUserId)
                                        .orElseThrow(
                                                 () -> new RuntimeException("Original AllMail user was not found."));

                                saveConnectedGoogleAccount(owner, oauthUser, authentication);

                                session.setAttribute(SessionKeys.ALLMAIL_USER_ID, owner.getId());
                                session.removeAttribute(SessionKeys.CONNECT_OWNER_USER_ID);

                                response.sendRedirect("http://localhost:5173/dashboard");
                                return;
                            }
                            
                            // Login flow: create/update the AllMail user and register their Gmail as connected
                            AppUser appUser = appUserService.createOrUpdateUser(oauthUser);

                            session.setAttribute(SessionKeys.ALLMAIL_USER_ID, appUser.getId());

                            saveConnectedGoogleAccount(appUser, oauthUser, authentication);

                            response.sendRedirect("http://localhost:5173/dashboard");
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:5173")
                        .permitAll()
                );

        return http.build();
    }
    
    /**
     * Wraps Spring's default OAuth request resolver so AllMail can add
     * Google-specific parameters before redirecting users to Google.
     */
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
                return customizeAuthorizationRequest(authorizationRequest, request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(
                    HttpServletRequest request,
                    String clientRegistrationId
            ) {
                OAuth2AuthorizationRequest authorizationRequest =
                        defaultResolver.resolve(request, clientRegistrationId);

                return customizeAuthorizationRequest(authorizationRequest, request);
            }
        };
    }

    /**
     * Adds OAuth parameters needed for persistent Gmail access and,
     * when connecting another mailbox, forces Google to show account selection.
     */
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request
    ) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, Object> additionalParameters =
                new HashMap<>(authorizationRequest.getAdditionalParameters());

        additionalParameters.put("access_type", "offline");
        additionalParameters.put("include_granted_scopes", "true");

        boolean isConnectingAnotherAccount =
                request.getSession().getAttribute(SessionKeys.CONNECT_OWNER_USER_ID) != null;

        if (isConnectingAnotherAccount) {
            additionalParameters.put("prompt", "select_account consent");
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .build();
    }
    
    /**
     * Saves or updates the Gmail account returned by Google OAuth
     * as a connected mailbox for the given user
     */
    private void saveConnectedGoogleAccount(
            AppUser appUser,
            OAuth2User oauthUser,
            org.springframework.security.core.Authentication authentication
    ) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String pictureUrl = oauthUser.getAttribute("picture");

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;

        LocalDateTime accessTokenExpiry = authorizedClient.getAccessToken().getExpiresAt() != null
                ? LocalDateTime.ofInstant(
                        authorizedClient.getAccessToken().getExpiresAt(),
                        ZoneId.systemDefault())
                : null;

        String scopes = String.join(",", authorizedClient.getAccessToken().getScopes());

        connectedAccountService.createOrUpdateGoogleAccount(
                appUser,
                googleId,
                email,
                name,
                pictureUrl,
                accessToken,
                refreshToken,
                accessTokenExpiry,
                scopes);
    }
    
     /**
     * Allows the local Vite frontend to call authenticated Spring Boot endpoints
     * while keeping browser cookies/session credentials enabled.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}