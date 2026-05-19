package com.rxshellg.allmail.service;

import com.rxshellg.allmail.model.ConnectedAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Refreshes stored Google access tokens for connected Gmail accounts.
 */
@Service
public class GoogleTokenService {

    private final ConnectedAccountService connectedAccountService;
    private final RestClient restClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    public GoogleTokenService(ConnectedAccountService connectedAccountService) {
        this.connectedAccountService = connectedAccountService;
        this.restClient = RestClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .build();
    }

    /**
     * Returns the account with a usable access token, refreshing it first if needed.
     */
    public ConnectedAccount getAccountWithValidAccessToken(ConnectedAccount account) {
        if (!shouldRefresh(account)) return account;
        
        String refreshToken = account.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank())
            throw new RuntimeException("No refresh token available for " + account.getEmailAddress());

        Map<String, Object> tokenResponse = restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("client_id=" + encode(googleClientId)
                        + "&client_secret=" + encode(googleClientSecret)
                        + "&refresh_token=" + encode(account.getRefreshToken())
                        + "&grant_type=refresh_token")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        account.setAccessToken((String) tokenResponse.get("access_token"));
        account.setAccessTokenExpiry(LocalDateTime.now().plusSeconds((Integer) tokenResponse.get("expires_in")));

        return connectedAccountService.save(account);
    }

    /**
     * Refreshes early so Gmail calls do not start with a nearly expired token.
     */
    private boolean shouldRefresh(ConnectedAccount account) {
        String token = account.getAccessToken();
        LocalDateTime expiry = account.getAccessTokenExpiry();
        return token == null || token.isBlank()
                || expiry == null
                || expiry.isBefore(LocalDateTime.now().plusMinutes(2));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}