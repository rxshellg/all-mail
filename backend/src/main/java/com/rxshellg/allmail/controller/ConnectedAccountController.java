package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.dto.ConnectedAccountDto;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.repository.AppUserRepository;
import com.rxshellg.allmail.service.ConnectedAccountService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    public List<ConnectedAccountDto> getConnectedAccounts(HttpSession session) {
        AppUser appUser = getCurrentAppUser(session);

        return connectedAccountService.getActiveAccountsForUser(appUser)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/api/accounts/connect/google")
    public void connectGoogleAccount(HttpSession session, HttpServletResponse response) throws IOException {
        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);

        if (appUserId == null) {
            throw new RuntimeException("You must be logged in before connecting another Google account.");
        }

        session.setAttribute(SessionKeys.CONNECT_OWNER_USER_ID, appUserId);

        response.sendRedirect("/oauth2/authorization/google");
    }

    private AppUser getCurrentAppUser(HttpSession session) {
        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);

        if (appUserId == null) {
            throw new RuntimeException("No AllMail user is stored in the current session.");
        }

        return appUserRepository.findById(appUserId)
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));
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