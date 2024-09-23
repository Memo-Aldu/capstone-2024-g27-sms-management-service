package com.crm.smsmanagementservice.provider.web;

import lombok.Builder;

import java.util.Set;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-18, Thursday
 */
@Builder
public record InboundMessageDTO(
     String messageStatus,
     String apiVersion,
     String to,
     String from,
     String body,
     String messageId,
     String accountId,
     String serviceId,
     String errorCode,
     String segments,
     String mediaLength,
     Set<String> sortedMediaTypes,
     Set<String> sortedMediaUrls,
     String errorMessage
) {}
