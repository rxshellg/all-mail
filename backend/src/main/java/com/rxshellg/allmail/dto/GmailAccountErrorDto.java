package com.rxshellg.allmail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GmailAccountErrorDto {
    private Long accountId;
    private String accountEmail;
    private String provider;
    private String message;
    private boolean reconnectRequired;
}