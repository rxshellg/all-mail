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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
 
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
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
 
    private static final String FRONTEND_URL = "http://localhost:5173";
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
     * Builds the main Spring Security filter chain:
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
                        .successHandler((request, response, authentication) ->
                                handleOAuthSuccess(request, response, authentication))
                )
                .logout(logout -> logout
                        .logoutSuccessUrl(FRONTEND_URL)
                        .permitAll()
                );
 
        return http.build();
    }
 
    /**
     * Handles the post-OAuth redirect: routes between the add-account and the 
     * login flow
     */
    private void handleOAuthSuccess(
            HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Authentication authentication
    ) throws java.io.IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        HttpSession session = request.getSession();
        Long connectOwnerUserId = (Long) session.getAttribute(SessionKeys.CONNECT_OWNER_USER_ID);
 
        if (connectOwnerUserId != null) {
            AppUser owner = appUserRepository.findById(connectOwnerUserId)
                    .orElseThrow(() -> new RuntimeException("Original AllMail user was not found."));
            Long reconnectAccountId = (Long) session.getAttribute(SessionKeys.RECONNECT_ACCOUNT_ID);
            persistGoogleAccount(owner, reconnectAccountId, oauthUser, authentication);
            session.removeAttribute(SessionKeys.RECONNECT_ACCOUNT_ID);
            session.setAttribute(SessionKeys.ALLMAIL_USER_ID, owner.getId());
            session.removeAttribute(SessionKeys.CONNECT_OWNER_USER_ID);
        } else {
            AppUser appUser = appUserService.createOrUpdateUser(oauthUser);
            session.setAttribute(SessionKeys.ALLMAIL_USER_ID, appUser.getId());
            persistGoogleAccount(appUser, null, oauthUser, authentication);
        }
 
        response.sendRedirect(FRONTEND_URL + "/dashboard");
    }
 
    /**
     * Wraps Spring's default OAuth request resolver to inject Google-specific
     * parameters before redirecting the user to Google
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
                return customizeAuthorizationRequest(defaultResolver.resolve(request), request);
            }
 
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                return customizeAuthorizationRequest(defaultResolver.resolve(request, clientRegistrationId), request);
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
            if (authorizationRequest == null)
                    return null;

            Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());
            additionalParameters.put("access_type", "offline");
            additionalParameters.put("include_granted_scopes", "true");

            if (request.getSession().getAttribute(SessionKeys.CONNECT_OWNER_USER_ID) != null) {
                    additionalParameters.put("prompt", "select_account consent");
            }

            return OAuth2AuthorizationRequest.from(authorizationRequest)
                            .additionalParameters(additionalParameters)
                            .build();
    }
    
    /**
    * Extracts Google account and token data from the OAuth result, then either
    * saves a newly connected mailbox or updates an existing one during reconnect.
    */
    private void persistGoogleAccount(AppUser owner, Long reconnectId, OAuth2User oauthUser, Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
 
        LocalDateTime accessTokenExpiry = client.getAccessToken().getExpiresAt() != null
                ? LocalDateTime.ofInstant(client.getAccessToken().getExpiresAt(), ZoneId.systemDefault())
                : null;
        String scopes = String.join(",", client.getAccessToken().getScopes());
        String refreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;
 
        if (reconnectId != null) {
            connectedAccountService.reconnectGoogleAccount(
                    owner, reconnectId,
                    oauthUser.getAttribute("sub"), oauthUser.getAttribute("email"),
                    oauthUser.getAttribute("name"), oauthUser.getAttribute("picture"),
                    client.getAccessToken().getTokenValue(), refreshToken, accessTokenExpiry, scopes);
        } else {
            connectedAccountService.createOrUpdateGoogleAccount(
                    owner,
                    oauthUser.getAttribute("sub"), oauthUser.getAttribute("email"),
                    oauthUser.getAttribute("name"), oauthUser.getAttribute("picture"),
                    client.getAccessToken().getTokenValue(), refreshToken, accessTokenExpiry, scopes);
        }
    }
 
    /**
     * Allows the local Vite frontend to call authenticated Spring Boot endpoints
     * while keeping browser cookies/session credentials enabled.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(FRONTEND_URL));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}