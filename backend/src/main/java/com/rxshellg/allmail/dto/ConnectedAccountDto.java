package com.rxshellg.allmail.dto;

public class ConnectedAccountDto {

    private Long id;
    private String provider;
    private String emailAddress;
    private String displayName;
    private String pictureUrl;
    private boolean active;

    public ConnectedAccountDto(Long id, String provider, String emailAddress,
                               String displayName, String pictureUrl, boolean active) {
        this.id = id;
        this.provider = provider;
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        this.pictureUrl = pictureUrl;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public boolean isActive() {
        return active;
    }
}