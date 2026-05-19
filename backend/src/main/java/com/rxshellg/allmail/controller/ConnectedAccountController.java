package com.rxshellg.allmail.controller;

import com.rxshellg.allmail.config.SessionKeys;
import com.rxshellg.allmail.dto.ConnectedAccountDto;
import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.service.ConnectedAccountService;
import com.rxshellg.allmail.service.CurrentUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Exposes the email accounts connected to the current AllMail user.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class ConnectedAccountController {

    private final CurrentUserService currentUserService;
    private final ConnectedAccountService connectedAccountService;

    @GetMapping
    public List<ConnectedAccountDto> getConnectedAccounts(HttpSession session) {
        return connectedAccountService.getActiveAccountsForUser(currentUserService.requireCurrentUser(session))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/connect/google")
    public void connectGoogleAccount(HttpSession session, HttpServletResponse response) throws IOException {
        session.setAttribute(SessionKeys.CONNECT_OWNER_USER_ID, currentUserService.requireCurrentUserId(session));
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/{accountId}/reconnect/google")
    public void reconnectGoogleAccount(
            @PathVariable Long accountId,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        AppUser appUser = currentUserService.requireCurrentUser(session);

        connectedAccountService.validateAccountBelongsToUser(appUser, accountId);

        session.setAttribute(SessionKeys.CONNECT_OWNER_USER_ID, appUser.getId());
        session.setAttribute(SessionKeys.RECONNECT_ACCOUNT_ID, accountId);

        response.sendRedirect("/oauth2/authorization/google");
    }

    @DeleteMapping("/{accountId}")
    public void disconnectAccount(@PathVariable Long accountId, HttpSession session) {
        connectedAccountService.disconnectAccount(currentUserService.requireCurrentUser(session), accountId);
    }

    private ConnectedAccountDto toDto(ConnectedAccount account) {
        return new ConnectedAccountDto(
                account.getId(),
                account.getProvider(),
                account.getEmailAddress(),
                account.getDisplayName(),
                account.getPictureUrl(),
                account.isActive(),
                account.isNeedsReconnect() || account.getRefreshToken() == null || account.getRefreshToken().isBlank()
        );
    }
}