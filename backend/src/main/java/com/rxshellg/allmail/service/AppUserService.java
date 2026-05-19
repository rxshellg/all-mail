package com.rxshellg.allmail.service;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.repository.AppUserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Handles the local AllMail user record created from Google OAuth data.
 */
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser createOrUpdateUser(OAuth2User oauthUser) {
        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String pictureUrl = oauthUser.getAttribute("picture");

        return appUserRepository.findByGoogleId(googleId)
                .map(user -> {
                    user.setEmail(email);
                    user.setName(name);
                    user.setPictureUrl(pictureUrl);
                    user.setLastLoginAt(LocalDateTime.now());
                    return appUserRepository.save(user);
                })
                .orElseGet(() -> appUserRepository.save(new AppUser(
                    googleId, email, name, pictureUrl
                )));
    }
}