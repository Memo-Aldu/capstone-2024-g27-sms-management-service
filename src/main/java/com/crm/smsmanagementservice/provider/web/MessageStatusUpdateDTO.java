package com.crm.smsmanagementservice.provider.web;

import lombok.Builder;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-20, Saturday
 */
@Builder
public record MessageStatusUpdateDTO(
     String messageStatus,
     String messageId,
     String accountId,
     String serviceId,
     String errorCode,
     String errorMessage
) {}
