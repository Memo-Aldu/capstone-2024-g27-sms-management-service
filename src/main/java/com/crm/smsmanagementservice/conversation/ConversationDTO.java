package com.crm.smsmanagementservice.conversation;

import lombok.Builder;

import java.time.ZonedDateTime;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@Builder
public record ConversationDTO(
        String id,
        String userId,
        String contactId,
        String conversationName,
        ConversationStatus status,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate
) {}
