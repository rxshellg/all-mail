package com.rxshellg.allmail.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Provides authentication-related API endpoints for the React frontend.
 */
@RestController
public class AuthController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "pictureUrl", principal.getAttribute("picture")
        );
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "AllMail backend is running.");
    }
}