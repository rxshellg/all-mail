package com.rxshellg.allmail.dto;

public class GmailAccountErrorDto {

    private Long accountId;
    private String accountEmail;
    private String provider;
    private String message;
    private boolean reconnectRequired;

    public GmailAccountErrorDto() {
    }

    public GmailAccountErrorDto(Long accountId, String accountEmail, String provider,
                                String message, boolean reconnectRequired) {
        this.accountId = accountId;
        this.accountEmail = accountEmail;
        this.provider = provider;
        this.message = message;
        this.reconnectRequired = reconnectRequired;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public String getProvider() {
        return provider;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReconnectRequired() {
        return reconnectRequired;
    }
}