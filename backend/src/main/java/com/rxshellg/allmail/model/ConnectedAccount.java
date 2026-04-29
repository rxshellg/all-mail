package com.rxshellg.allmail.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a Gmail mailbox connected to a user
 */
@Entity
@Table(
        name = "connected_accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"app_user_id", "provider", "provider_account_id"})
        }
)
public class ConnectedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The AllMail user who owns this connected mailbox
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_account_id", nullable = false)
    private String providerAccountId;

    @Column(nullable = false)
    private String emailAddress;

    private String displayName;

    private String pictureUrl;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private LocalDateTime accessTokenExpiry;

    @Column(columnDefinition = "TEXT")
    private String scopes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime connectedAt;

    private LocalDateTime lastSyncedAt;

    public ConnectedAccount() {
    }

    public ConnectedAccount(AppUser appUser, String provider, String providerAccountId,
                                 String emailAddress, String displayName, String pictureUrl) {
        this.appUser = appUser;
        this.provider = provider;
        this.providerAccountId = providerAccountId;
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        this.pictureUrl = pictureUrl;
        this.connectedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (connectedAt == null) {
            connectedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderAccountId() {
        return providerAccountId;
    }

    public void setProviderAccountId(String providerAccountId) {
        this.providerAccountId = providerAccountId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(LocalDateTime accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    @Override
    public String toString() {
        return "ConnectedAccount{" +
                "id=" + id +
                ", provider='" + provider + '\'' +
                ", providerAccountId='" + providerAccountId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", displayName='" + displayName + '\'' +
                ", active=" + active +
                ", connectedAt=" + connectedAt +
                ", lastSyncedAt=" + lastSyncedAt +
                '}';
    }
}