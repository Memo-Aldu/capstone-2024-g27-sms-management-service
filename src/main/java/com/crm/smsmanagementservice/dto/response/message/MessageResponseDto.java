package com.crm.smsmanagementservice.dto.response.message;

import com.crm.smsmanagementservice.enums.MessageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/1/2024, Saturday
 */
@Builder
public record MessageResponseDto(
    @JsonProperty("id")
    String id,
    @JsonProperty("content")
    String messageContent,
    @JsonProperty("conversationId")
    String conversationId,
    @JsonProperty("status")
    MessageStatus status,
    @JsonProperty("deliveredTime")
    ZonedDateTime deliveredTime,
    @JsonProperty("scheduledTime")
    ZonedDateTime scheduledTime,
    @JsonProperty("media")
    Map<String, String> mediaUrls) {}
