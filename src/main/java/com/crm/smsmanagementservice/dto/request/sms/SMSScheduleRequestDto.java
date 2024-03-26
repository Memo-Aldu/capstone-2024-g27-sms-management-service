package com.crm.smsmanagementservice.dto.request.sms;

import com.crm.smsmanagementservice.util.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
public record SMSScheduleRequestDto(
        @NotNull(message = "Sender cannot be null")
        @ValidPhoneNumber(message = "Sender is not a valid phone number")
        @JsonProperty("sender")
        String sender,

        @NotNull(message = "Recipient cannot be null")
        @ValidPhoneNumber(message = "Recipient is not a valid phone number")
        @JsonProperty("recipient") String recipient,

        @NotNull(message = "Message content cannot be null")
        @NotBlank(message = "Message content cannot be blank")
        @JsonProperty("content") String messageContent,

        @NotNull(message = "Date time cannot be null")
        @Future(message = "Date time cannot be in the past")
        //TODO: Validate date between interval?
        @JsonProperty("scheduleTime") ZonedDateTime scheduleTime
) {}