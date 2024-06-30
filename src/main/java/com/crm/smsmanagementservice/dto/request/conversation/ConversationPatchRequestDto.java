package com.crm.smsmanagementservice.dto.request.conversation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
public record ConversationPatchRequestDto(
        @JsonProperty("conversationName")
        @NotNull(message = "Conversation name is required")
        @NotBlank(message = "Conversation name is required")
        String conversationName
) {}
