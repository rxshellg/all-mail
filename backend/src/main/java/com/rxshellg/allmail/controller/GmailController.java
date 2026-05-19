package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.dto.GmailInboxResponseDto;
import com.rxshellg.allmail.service.ConnectedAccountService;
import com.rxshellg.allmail.service.GmailService;
import com.rxshellg.allmail.service.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes Gmail inbox data for the current user's connected accounts
 */
@RequiredArgsConstructor
@RestController
public class GmailController {

    private final GmailService gmailService;
    private final CurrentUserService currentUserService;
    private final ConnectedAccountService connectedAccountService;

    @GetMapping("/api/gmail/messages")
    public GmailInboxResponseDto getInboxMessages(HttpSession session) {
        return gmailService.getInboxMessages(
                connectedAccountService.getActiveAccountsForUser(
                        currentUserService.requireCurrentUser(session)));
    }
}