package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.dto.GmailMessageDto;
import com.rxshellg.allmail.service.GmailService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes Gmail inbox data for the authenticated Google user
 */
@RestController
public class GmailController {

    private final GmailService gmailService;

    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping("/api/gmail/messages")
    public List<GmailMessageDto> getInboxMessages(OAuth2AuthenticationToken authentication) {
        return gmailService.getInboxMessages(authentication);
    }
}