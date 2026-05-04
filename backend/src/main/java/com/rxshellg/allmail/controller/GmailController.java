package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.dto.GmailInboxResponseDto;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.repository.AppUserRepository;
import com.rxshellg.allmail.service.ConnectedAccountService;
import com.rxshellg.allmail.service.GmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes Gmail inbox data for the current user's connected accounts
 */
@RestController
public class GmailController {

    private final GmailService gmailService;
    private final AppUserRepository appUserRepository;
    private final ConnectedAccountService connectedAccountService;

    public GmailController(
            GmailService gmailService,
            AppUserRepository appUserRepository,
            ConnectedAccountService connectedAccountService
    ) {
        this.gmailService = gmailService;
        this.appUserRepository = appUserRepository;
        this.connectedAccountService = connectedAccountService;
    }

    @GetMapping("/api/gmail/messages")
    public GmailInboxResponseDto getInboxMessages(HttpSession session) {
        AppUser appUser = getCurrentAppUser(session);
        List<ConnectedAccount> connectedAccounts =
                connectedAccountService.getActiveAccountsForUser(appUser);

        return gmailService.getInboxMessages(connectedAccounts);
    }

    private AppUser getCurrentAppUser(HttpSession session) {
        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);

        if (appUserId == null) {
            throw new RuntimeException("No AllMail user is stored in the current session.");
        }

        return appUserRepository.findById(appUserId)
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));
    }
}