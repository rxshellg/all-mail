package com.rxshellg.allmail.config;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.service.AppUserService;
import com.rxshellg.allmail.service.ConnectedAccountService;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Configures Google OAuth login, protected backend routes,
 * and local React-to-Spring communication during development.
 */
@Configuration
public class SecurityConfig {

    private final AppUserService appUserService;
    private final ConnectedAccountService connectedAccountService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityConfig(
            AppUserService appUserService,
            ConnectedAccountService connectedAccountService,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        this.appUserService = appUserService;
        this.connectedAccountService = connectedAccountService;
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

                            // Create/update the AllMail user, then register their Gmail as a connected mailbox
                            AppUser appUser = appUserService.createOrUpdateUser(oauthUser);
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

    private void saveConnectedGoogleAccount(
            AppUser appUser,
            OAuth2User oauthUser,
            org.springframework.security.core.Authentication authentication
    ) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String pictureUrl = oauthUser.getAttribute("picture");

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;

        LocalDateTime accessTokenExpiresAt = authorizedClient.getAccessToken().getExpiresAt() != null
                ? LocalDateTime.ofInstant(
                        authorizedClient.getAccessToken().getExpiresAt(),
                        ZoneId.systemDefault()
                )
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
                accessTokenExpiresAt,
                scopes
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow the frontend to call authenticated backend endpoints.
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}