package com.crm.smsmanagementservice.message.web;

import com.crm.smsmanagementservice.core.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
public record MessageCreateDTO(
        @NotNull(message = "User ID cannot be null")
        @NotBlank(message = "User ID cannot be blank")
        @JsonProperty("userId") String userId,

        @NotNull(message = "From cannot be null")
        @NotBlank(message = "From cannot be blank")
        @ValidPhoneNumber(message = "Invalid phone number")
        @JsonProperty("from") String from,

        @Nullable
        @Future(message = "Date time cannot be in the past")
        @JsonProperty("scheduledDate") ZonedDateTime scheduledDate,

        @NotNull.List({@NotNull(message = "Must have at least 1 item ")})
        @Size(min = 1, max = 20, message = "Must have between 1 and 20 message items")
        @JsonProperty("messageItems") List<@Valid MessageItemDTO> messageItems,

        @Nullable
        @Size(max = 5, message = "Media cannot have more than 5 items")
        @JsonProperty("media") List<String> media
) {

    @Builder
    record MessageItemDTO(
            @NotNull(message = "Message content cannot be null")
            @NotBlank(message = "Message content cannot be blank")
            @JsonProperty("content") String content,
            @NotNull(message = "Contact ID cannot be null")
            @JsonProperty("contactId") String contactId,
            @ValidPhoneNumber(message = "Invalid phone number")
            @NotNull(message = "Recipient cannot be null")
            @JsonProperty("to") String to) {}
}


