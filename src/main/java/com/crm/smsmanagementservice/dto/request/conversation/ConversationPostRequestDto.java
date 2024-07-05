package com.crm.smsmanagementservice.dto.request.conversation;

import com.crm.smsmanagementservice.util.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
public record ConversationPostRequestDto(
        @JsonProperty("sender")
        @NotNull(message = "Sender cannot be null")
        @ValidPhoneNumber(message = "Sender is not a valid phone number")
        String sender,

        @JsonProperty("recipient")
        @NotNull(message = "Recipient cannot be null")
        @ValidPhoneNumber(message = "Recipient is not a valid phone number")
        String recipient,

        @JsonProperty("conversationName")
        String conversationName
) {}
