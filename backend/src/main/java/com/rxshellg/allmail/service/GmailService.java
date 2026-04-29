package com.rxshellg.allmail.service;

import com.rxshellg.allmail.dto.GmailMessageDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Uses the logged-in user's Google access token to read Gmail inbox data.
 */
@Service
public class GmailService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient restClient;

    public GmailService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.restClient = RestClient.builder()
                .baseUrl("https://gmail.googleapis.com/gmail/v1")
                .build();
    }

    public List<GmailMessageDto> getInboxMessages(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();

        Map<String, Object> listResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/messages")
                        .queryParam("maxResults", 10)
                        .queryParam("labelIds", "INBOX")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        List<Map<String, Object>> messages =
                (List<Map<String, Object>>) listResponse.getOrDefault("messages", List.of());

        List<GmailMessageDto> inboxMessages = new ArrayList<>();

        for (Map<String, Object> message : messages) {
            String messageId = (String) message.get("id");
            inboxMessages.add(getMessageDetails(messageId, accessToken));
        }

        return inboxMessages;
    }

    private GmailMessageDto getMessageDetails(String messageId, String accessToken) {
        Map<String, Object> messageDetails = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/messages/{messageId}")
                        .queryParam("format", "metadata")
                        .queryParam("metadataHeaders", "From")
                        .queryParam("metadataHeaders", "Subject")
                        .queryParam("metadataHeaders", "Date")
                        .build(messageId))
                .header("Authorization", "Bearer " + accessToken)
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
                from,
                subject,
                snippet,
                receivedAt,
                unread
        );
    }

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