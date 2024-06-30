package com.crm.smsmanagementservice.dto.response.mms;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;

/**
 * This class represents a data transfer object for outgoing bulk MMS send responses.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@Builder
public record MMSBulkSendResponseDto(
        @Valid
        @JsonProperty("messages") List<MMSSendResponseDto> messages
) {}
