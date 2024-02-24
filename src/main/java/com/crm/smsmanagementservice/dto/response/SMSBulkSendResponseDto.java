package com.crm.smsmanagementservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
public record SMSBulkSendResponseDto(
        @Valid
        @JsonProperty("messages") List<SMSSendResponseDto> messages
) {}
