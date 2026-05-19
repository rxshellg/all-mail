package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.service.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Provides authentication-related API endpoints for the React frontend
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUserService currentUserService;

    @GetMapping("/api/auth/me")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        AppUser appUser = currentUserService.requireCurrentUser(session);

        return Map.of(
                "name", appUser.getName(),
                "email", appUser.getEmail(),
                "pictureUrl", appUser.getPictureUrl() == null ? "" : appUser.getPictureUrl()
        );
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "AllMail backend is running.");
    }
}