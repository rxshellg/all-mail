package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Provides authentication-related API endpoints for the React frontend.
 */
@RestController
public class AuthController {

    private final AppUserRepository appUserRepository;

    public AuthController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/api/auth/me")
    public Map<String, Object> getCurrentUser(HttpSession session) {

        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);

        if (appUserId == null) {
            throw new RuntimeException("No AllMail user is stored in the current session.");
        }

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));

        return Map.of(
                "name", appUser.getName(),
                "email", appUser.getEmail(),
                "pictureUrl", appUser.getPictureUrl()
        );
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "AllMail backend is running.");
    }
}