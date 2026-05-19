package com.rxshellg.allmail.service;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.repository.ConnectedAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConnectedAccountService {

    private final ConnectedAccountRepository connectedAccountRepository;

    public ConnectedAccount createOrUpdateGoogleAccount(
            AppUser appUser,
            String googleId,
            String email,
            String name,
            String pictureUrl,
            String accessToken,
            String refreshToken,
            LocalDateTime accessTokenExpiry,
            String scopes
    ) {
        ConnectedAccount account = connectedAccountRepository
                .findByAppUserAndProviderAndProviderAccountId(appUser, "GOOGLE", googleId)
                .orElseGet(() -> new ConnectedAccount(appUser, "GOOGLE", googleId, email, name, pictureUrl));

        return connectedAccountRepository.save(
                applyGoogleTokenFields(account, email, name, pictureUrl, accessToken, refreshToken, accessTokenExpiry, scopes));
    }

    @Transactional(readOnly = true)
    public List<ConnectedAccount> getActiveAccountsForUser(AppUser appUser) {
        return connectedAccountRepository.findByAppUserAndActiveTrue(appUser);
    }

    public ConnectedAccount save(ConnectedAccount account) {
        return connectedAccountRepository.save(account);
    }

    public void disconnectAccount(AppUser appUser, Long accountId) {
        ConnectedAccount account = findAndVerifyOwnership(appUser, accountId);
        account.setActive(false);
    }

    public void validateAccountBelongsToUser(AppUser appUser, Long accountId) {
        findAndVerifyOwnership(appUser, accountId);
    }

    public ConnectedAccount reconnectGoogleAccount(
            AppUser appUser,
            Long accountId,
            String googleId,
            String email,
            String name,
            String pictureUrl,
            String accessToken,
            String refreshToken,
            LocalDateTime accessTokenExpiry,
            String scopes
    ) {
        ConnectedAccount account = findAndVerifyOwnership(appUser, accountId);

        if (!"GOOGLE".equalsIgnoreCase(account.getProvider())) {
            throw new RuntimeException("Only Google accounts can be reconnected here.");
        }

        if (!account.getProviderAccountId().equals(googleId)) {
            throw new RuntimeException("Selected Google account does not match the account being reconnected.");
        }

        return connectedAccountRepository.save(
                applyGoogleTokenFields(account, email, name, pictureUrl, accessToken, refreshToken, accessTokenExpiry, scopes));
    }

    // --- Private helpers ---

    private ConnectedAccount findAndVerifyOwnership(AppUser appUser, Long accountId) {
        ConnectedAccount account = connectedAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Connected account was not found."));

        if (!account.getAppUser().getId().equals(appUser.getId())) {
            throw new RuntimeException("Connected account does not belong to the current user.");
        }

        return account;
    }

    private ConnectedAccount applyGoogleTokenFields(
        ConnectedAccount account,
        String email,
        String name,
        String pictureUrl,
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiry,
        String scopes
    ) {
        account.setEmailAddress(email);
        account.setDisplayName(name);
        account.setPictureUrl(pictureUrl);
        account.setAccessToken(accessToken);
        account.setAccessTokenExpiry(accessTokenExpiry);
        account.setScopes(scopes);
        account.setActive(true);

        if (refreshToken != null && !refreshToken.isBlank()) {
            account.setRefreshToken(refreshToken);
            account.setNeedsReconnect(false);
        }
        return account;
    }
}