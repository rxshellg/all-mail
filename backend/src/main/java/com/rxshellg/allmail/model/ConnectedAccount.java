package com.rxshellg.allmail.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * Represents a Gmail mailbox connected to a user
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"accessToken", "refreshToken", "scopes"})
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

    @Column(nullable = false)
    private boolean needsReconnect = false;

    @Column(nullable = false, updatable = false)
    @Setter(lombok.AccessLevel.NONE)
    private LocalDateTime connectedAt;

    private LocalDateTime lastSyncedAt;

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
        if (connectedAt == null) connectedAt = LocalDateTime.now();
    }
}