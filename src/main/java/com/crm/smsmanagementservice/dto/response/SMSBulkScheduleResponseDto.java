package com.crm.smsmanagementservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Fridays
 */
public record SMSBulkScheduleResponseDto(
        @Valid
        @JsonProperty("messages") List<SMSSendResponseDto> messages,

        @NotNull(message = "Date time cannot be null")
        @NotBlank(message = "Date time cannot be blank")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("scheduleTime") LocalDateTime scheduleTime
) {}
