package com.rxshellg.allmail.dto;

import java.util.List;

public class GmailInboxResponseDto {

    private List<GmailMessageDto> messages;
    private List<GmailAccountErrorDto> errors;

    public GmailInboxResponseDto() {
    }

    public GmailInboxResponseDto(List<GmailMessageDto> messages, List<GmailAccountErrorDto> errors) {
        this.messages = messages;
        this.errors = errors;
    }

    public List<GmailMessageDto> getMessages() {
        return messages;
    }

    public List<GmailAccountErrorDto> getErrors() {
        return errors;
    }
}