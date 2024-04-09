package com.crm.smsmanagementservice.dto.response.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.util.List;

/**
 * This class represents a data transfer object for outgoing bulk SMS send responses.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
public record SMSBulkSendResponseDto(
        @Valid
        @JsonProperty("messages") List<SMSSendResponseDto> messages
) {}
