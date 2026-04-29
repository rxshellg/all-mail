package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.dto.ConnectedAccountDto;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.repository.AppUserRepository;
import com.rxshellg.allmail.service.ConnectedAccountService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the email accounts connected to the current AllMail user
 */
@RestController
public class ConnectedAccountController {

    private final AppUserRepository appUserRepository;
    private final ConnectedAccountService connectedAccountService;

    public ConnectedAccountController(
            AppUserRepository appUserRepository,
            ConnectedAccountService connectedAccountService
    ) {
        this.appUserRepository = appUserRepository;
        this.connectedAccountService = connectedAccountService;
    }

    @GetMapping("/api/accounts")
    public List<ConnectedAccountDto> getConnectedAccounts(@AuthenticationPrincipal OAuth2User principal) {
        String googleId = principal.getAttribute("sub");

        AppUser appUser = appUserRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));

        return connectedAccountService.getActiveAccountsForUser(appUser)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ConnectedAccountDto toDto(ConnectedAccount account) {
        return new ConnectedAccountDto(
                account.getId(),
                account.getProvider(),
                account.getEmailAddress(),
                account.getDisplayName(),
                account.getPictureUrl(),
                account.isActive()
        );
    }
}