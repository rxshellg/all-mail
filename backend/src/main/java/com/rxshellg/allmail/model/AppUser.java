package com.rxshellg.allmail.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String googleId;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String pictureUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime lastLoginAt;

    public AppUser(String googleId, String email, String name, String pictureUrl) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (lastLoginAt == null) lastLoginAt = now;
    }
}