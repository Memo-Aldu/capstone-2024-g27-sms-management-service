package com.crm.smsmanagementservice.dto.response.mms;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * This class represents a data transfer object for outgoing scheduling bulk MMS responses.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Fridays
 */
@Builder
public record MMSBulkScheduleResponseDto(
        @Valid
        @JsonProperty("messages") List<MMSSendResponseDto> messages,

        @NotNull(message = "Date time cannot be null")
        @NotBlank(message = "Date time cannot be blank")
        @JsonProperty("scheduledTime") ZonedDateTime scheduledTime
) {}