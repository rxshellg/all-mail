package com.rxshellg.allmail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GmailInboxResponseDto {
    private List<GmailMessageDto> messages;
    private List<GmailAccountErrorDto> errors;
}