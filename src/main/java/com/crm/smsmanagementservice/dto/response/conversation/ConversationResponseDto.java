package com.crm.smsmanagementservice.dto.response.conversation;

import com.crm.smsmanagementservice.util.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.ZonedDateTime;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Builder
public record ConversationResponseDto(
        @JsonProperty("id")
        @NotNull(message = "Conversation ID is required")
        @NotBlank(message = "Conversation ID is required")
        String id,

        @JsonProperty("sender")
        @NotNull(message = "Sender cannot be null")
        @ValidPhoneNumber(message = "Sender is not a valid phone number")
        String sender,

        @JsonProperty("recipients")
        @NotNull(message = "Recipient cannot be null")
        @ValidPhoneNumber(message = "Recipient is not a valid phone number")
        String recipient,

        @JsonProperty("conversationName")
        String conversationName,

        @NotNull(message = "Creation Date time cannot be null")
        @NotBlank(message = "Creation Date time cannot be blank")
        @JsonProperty("createdTime")
        ZonedDateTime createdTime,

        @JsonProperty("updatedTime")
        @NotNull(message = "Update Date time cannot be null")
        @NotBlank(message = "Update Date time cannot be blank")
        ZonedDateTime updatedTime
) {}
