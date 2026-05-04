package com.rxshellg.allmail.service;

import com.rxshellg.allmail.dto.GmailAccountErrorDto;
import com.rxshellg.allmail.dto.GmailInboxResponseDto;
import com.rxshellg.allmail.dto.GmailMessageDto;
import com.rxshellg.allmail.model.ConnectedAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reads Gmail messages from the Google accounts connected to an AllMail user
 */
@Service
public class GmailService {

    private static final Logger logger = LoggerFactory.getLogger(GmailService.class);
    private final RestClient restClient;
    private final GoogleTokenService googleTokenService;

    public GmailService(GoogleTokenService googleTokenService) {
        this.googleTokenService = googleTokenService;
        this.restClient = RestClient.builder()
                .baseUrl("https://gmail.googleapis.com/gmail/v1")
                .build();
    }

    /**
     * Fetches inbox messages for every active connected Google account.
     */
    public GmailInboxResponseDto getInboxMessages(List<ConnectedAccount> connectedAccounts) {
        List<GmailMessageDto> allMessages = new ArrayList<>();
        List<GmailAccountErrorDto> errors = new ArrayList<>();

        for (ConnectedAccount account : connectedAccounts) {
            if (!"GOOGLE".equalsIgnoreCase(account.getProvider())) {
                continue;
            }

            try {
                allMessages.addAll(getInboxMessagesForAccount(account));
            } catch (Exception ex) {
                logger.warn(
                        "Could not load Gmail messages for connected account {}",
                        account.getEmailAddress(),
                        ex
                );

                errors.add(buildAccountError(account, ex));
            }
        }

        return new GmailInboxResponseDto(allMessages, errors);
    }

    /**
     * Gets a small list of message IDs for one Gmail account, then loads metadata for each.
     */
    private List<GmailMessageDto> getInboxMessagesForAccount(ConnectedAccount account) {
        account = googleTokenService.getAccountWithValidAccessToken(account);
        
        Map<String, Object> listResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/messages")
                        .queryParam("maxResults", 10)
                        .queryParam("labelIds", "INBOX")
                        .build())
                .header("Authorization", "Bearer " + account.getAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        List<Map<String, Object>> messages =
                listResponse == null
                        ? List.of()
                        : (List<Map<String, Object>>) listResponse.getOrDefault("messages", List.of());

        List<GmailMessageDto> accountMessages = new ArrayList<>();

        for (Map<String, Object> message : messages) {
            String messageId = (String) message.get("id");
            accountMessages.add(getMessageDetails(messageId, account));
        }

        return accountMessages;
    }

    /**
     * Loads the sender, subject, date, snippet, and unread status for one Gmail message
     */
    private GmailMessageDto getMessageDetails(String messageId, ConnectedAccount account) {
        Map<String, Object> messageDetails = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/messages/{messageId}")
                        .queryParam("format", "metadata")
                        .queryParam("metadataHeaders", "From")
                        .queryParam("metadataHeaders", "Subject")
                        .queryParam("metadataHeaders", "Date")
                        .build(messageId))
                .header("Authorization", "Bearer " + account.getAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        String threadId = (String) messageDetails.get("threadId");
        String snippet = (String) messageDetails.getOrDefault("snippet", "");
        String from = getHeaderValue(messageDetails, "From");
        String subject = getHeaderValue(messageDetails, "Subject");
        String receivedAt = getHeaderValue(messageDetails, "Date");

        List<String> labelIds = (List<String>) messageDetails.getOrDefault("labelIds", List.of());
        boolean unread = labelIds.contains("UNREAD");

        return new GmailMessageDto(
                messageId,
                threadId,
                account.getId(),
                account.getEmailAddress(),
                account.getDisplayName(),
                from,
                subject,
                snippet,
                receivedAt,
                unread
        );
    }

    /**
     * Converts account-specific Gmail/token failures into safe frontend messages
     */
    private GmailAccountErrorDto buildAccountError(ConnectedAccount account, Exception ex) {
        boolean reconnectRequired = isReconnectRequired(ex);

        String message = reconnectRequired
                ? "This account needs to be reconnected."
                : "Messages could not be loaded for this account.";

        return new GmailAccountErrorDto(
                account.getId(),
                account.getEmailAddress(),
                account.getProvider(),
                message,
                reconnectRequired
        );
    }

    /**
     * Detects failures that usually mean Google access was revoked or cannot be refreshed
     */
    private boolean isReconnectRequired(Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("No refresh token available")) {
            return true;
        }

        if (ex instanceof RestClientResponseException responseException) {
            int statusCode = responseException.getStatusCode().value();
            return statusCode == 400 || statusCode == 401 || statusCode == 403;
        }

        return false;
    }

    /**
     * Finds a specific Gmail metadata header from the message payload
     */
    private String getHeaderValue(Map<String, Object> messageDetails, String headerName) {
        Map<String, Object> payload = (Map<String, Object>) messageDetails.get("payload");
        List<Map<String, String>> headers =
                (List<Map<String, String>>) payload.getOrDefault("headers", List.of());

        return headers.stream()
                .filter(header -> headerName.equalsIgnoreCase(header.get("name")))
                .map(header -> header.get("value"))
                .findFirst()
                .orElse("");
    }
}