package com.crm.smsmanagementservice.message;

import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@Builder
public record MessageDTO(
        String id,
        String conversationId,
        String to,
        String from,
        Integer messageSegmentCount,
        BigDecimal price,
        String contactId,
        String userId,
        String country,
        String carrier,
        MessageStatus status,
        String content,
        @Nullable
        List<String> media,
        Currency currency,
        MessageDirection direction,
        MessageType type,
        @Nullable
        ZonedDateTime scheduledDate,
        ZonedDateTime createdDate,
        @Nullable
        ZonedDateTime deliveredTime
) {}




