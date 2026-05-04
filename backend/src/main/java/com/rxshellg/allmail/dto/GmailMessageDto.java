package com.rxshellg.allmail.dto;

public class GmailMessageDto {

    private String id;
    private String threadId;
    private Long accountId;
    private String accountEmail;
    private String accountDisplayName;
    private String from;
    private String subject;
    private String snippet;
    private String receivedAt;
    private boolean unread;

    public GmailMessageDto() {
    }

    public GmailMessageDto(String id, String threadId, Long accountId, String accountEmail,
                           String accountDisplayName, String from, String subject,
                           String snippet, String receivedAt, boolean unread) {
        this.id = id;
        this.threadId = threadId;
        this.accountId = accountId;
        this.accountEmail = accountEmail;
        this.accountDisplayName = accountDisplayName;
        this.from = from;
        this.subject = subject;
        this.snippet = snippet;
        this.receivedAt = receivedAt;
        this.unread = unread;
    }

    public String getId() {
        return id;
    }

    public String getThreadId() {
        return threadId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public String getAccountDisplayName() {
        return accountDisplayName;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public boolean isUnread() {
        return unread;
    }
}