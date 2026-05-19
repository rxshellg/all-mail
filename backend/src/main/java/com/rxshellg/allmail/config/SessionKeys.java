package com.rxshellg.allmail.config;

/**
 * Session keys used to separate the AllMail user from temporary OAuth flows
 */
public final class SessionKeys {

    public static final String ALLMAIL_USER_ID = "ALLMAIL_USER_ID";
    public static final String CONNECT_OWNER_USER_ID = "CONNECT_OWNER_USER_ID";
    public static final String RECONNECT_ACCOUNT_ID = "RECONNECT_ACCOUNT_ID";

    private SessionKeys() {
    }
}