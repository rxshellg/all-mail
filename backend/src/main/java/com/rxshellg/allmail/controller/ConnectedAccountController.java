package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.dto.ConnectedAccountDto;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.service.ConnectedAccountService;
import com.rxshellg.allmail.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Exposes the email accounts connected to the current AllMail user.
 */
@RestController
@RequestMapping("/api/accounts")
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

    @GetMapping
    public List<ConnectedAccountDto> getConnectedAccounts(HttpSession session) {
        return connectedAccountService.getActiveAccountsForUser(getCurrentAppUser(session))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/connect/google")
    public void connectGoogleAccount(HttpSession session, HttpServletResponse response) throws IOException {
        session.setAttribute(SessionKeys.CONNECT_OWNER_USER_ID, requireAppUserId(session));
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/{accountId}/reconnect/google")
    public void reconnectGoogleAccount(
            @PathVariable Long accountId,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        AppUser appUser = getCurrentAppUser(session);

        connectedAccountService.validateAccountBelongsToUser(appUser, accountId);

        session.setAttribute(SessionKeys.CONNECT_OWNER_USER_ID, appUser.getId());
        session.setAttribute(SessionKeys.RECONNECT_ACCOUNT_ID, accountId);

        response.sendRedirect("/oauth2/authorization/google");
    }

    @DeleteMapping("/{accountId}")
    public void disconnectAccount(@PathVariable Long accountId, HttpSession session) {
        connectedAccountService.disconnectAccount(getCurrentAppUser(session), accountId);
    }

    // --- Helpers ---

    private Long requireAppUserId(HttpSession session) {
        Long appUserId = (Long) session.getAttribute(SessionKeys.ALLMAIL_USER_ID);
        if (appUserId == null) throw new RuntimeException("No AllMail user is stored in the current session.");
        return appUserId;
    }

    private AppUser getCurrentAppUser(HttpSession session) {
        return appUserRepository.findById(requireAppUserId(session))
                .orElseThrow(() -> new RuntimeException("Logged-in user was not found."));
    }

    private ConnectedAccountDto toDto(ConnectedAccount account) {
        boolean needsReconnect =
            account.isNeedsReconnect()
                || account.getRefreshToken() == null 
                || account.getRefreshToken().isBlank();

        return new ConnectedAccountDto(
                account.getId(),
                account.getProvider(),
                account.getEmailAddress(),
                account.getDisplayName(),
                account.getPictureUrl(),
                account.isActive(),
                needsReconnect
        );
    }
}