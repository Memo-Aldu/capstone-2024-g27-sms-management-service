package com.crm.smsmanagementservice.dto.response.sms;

import com.crm.smsmanagementservice.enums.MessageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
public record SMSScheduleResponseDto(
        @NotNull(message = "Message ID is required")
        @NotBlank(message = "Message ID is required")
        @JsonProperty("id") String messageId,

        @NotNull(message = "Status is required")
        @NotBlank(message = "Status is required")
        @JsonProperty("status") MessageStatus status,

        @NotNull(message = "Date time cannot be null")
        @NotBlank(message = "Date time cannot be blank")
        @JsonProperty("scheduleTime") ZonedDateTime scheduleTime
) {}