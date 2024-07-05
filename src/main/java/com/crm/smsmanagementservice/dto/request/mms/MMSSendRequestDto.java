package com.crm.smsmanagementservice.dto.request.mms;

import com.crm.smsmanagementservice.util.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * This class represents a data transfer object for incoming MMS send requests.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/20/2024, Tuesday
 */
public record MMSSendRequestDto(
        @NotNull(message = "Sender cannot be null")
        @ValidPhoneNumber(message = "Sender is not a valid phone number")
        @JsonProperty("sender") String sender,

        @NotNull(message = "Recipient cannot be null")
        @ValidPhoneNumber(message = "Recipient is not a valid phone number")
        @JsonProperty("recipient") String recipient,

        @NotNull(message = "Message content cannot be null")
        @NotBlank(message = "Message content cannot be blank")
        @JsonProperty("content") String messageContent,

        @NotNull(message = "Media cannot be null")
        @NotEmpty(message = "Media cannot be empty")
        @JsonProperty("media") List<String> mediaUrls,
        @JsonProperty("conversationId") String conversationId
) {}