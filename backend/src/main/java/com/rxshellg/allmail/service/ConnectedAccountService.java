package com.rxshellg.allmail.service;

import com.rxshellg.allmail.model.AppUser;
import com.rxshellg.allmail.model.ConnectedAccount;
import com.rxshellg.allmail.repository.ConnectedAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConnectedAccountService {

    private final ConnectedAccountRepository connectedAccountRepository;

    public ConnectedAccountService(ConnectedAccountRepository connectedAccountRepository) {
        this.connectedAccountRepository = connectedAccountRepository;
    }

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
        return connectedAccountRepository
                .findByAppUserAndProviderAndProviderAccountId(appUser, "GOOGLE", googleId)
                .map(existingAccount -> {
                    existingAccount.setEmailAddress(email);
                    existingAccount.setDisplayName(name);
                    existingAccount.setPictureUrl(pictureUrl);
                    existingAccount.setAccessToken(accessToken);

                    if (refreshToken != null) {
                        existingAccount.setRefreshToken(refreshToken);
                    }

                    existingAccount.setAccessTokenExpiry(accessTokenExpiry);
                    existingAccount.setScopes(scopes);
                    existingAccount.setActive(true);

                    return connectedAccountRepository.save(existingAccount);
                })
                .orElseGet(() -> {
                    ConnectedAccount newAccount = new ConnectedAccount(
                            appUser,
                            "GOOGLE",
                            googleId,
                            email,
                            name,
                            pictureUrl
                    );

                    newAccount.setAccessToken(accessToken);
                    newAccount.setRefreshToken(refreshToken);
                    newAccount.setAccessTokenExpiry(accessTokenExpiry);
                    newAccount.setScopes(scopes);

                    return connectedAccountRepository.save(newAccount);
                });
    }

    public java.util.List<ConnectedAccount> getActiveAccountsForUser(AppUser appUser) {
        return connectedAccountRepository.findByAppUserAndActiveTrue(appUser);
    }
}