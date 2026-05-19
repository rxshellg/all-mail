package com.rxshellg.allmail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectedAccountDto {
    private Long id;
    private String provider;
    private String emailAddress;
    private String displayName;
    private String pictureUrl;
    private boolean active;
    private boolean needsReconnect;
}