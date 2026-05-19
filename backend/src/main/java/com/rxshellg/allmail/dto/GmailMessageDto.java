package com.rxshellg.allmail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}