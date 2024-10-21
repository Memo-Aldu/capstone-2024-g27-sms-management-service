package com.crm.smsmanagementservice.provider;

import com.crm.smsmanagementservice.core.dto.DomainMessage;

import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
public interface MessagingProviderInternalAPI {
    Map<String, DomainMessage> sendMessages(ProviderMessagingDTO messages);
    Map<String, DomainMessage> scheduleMessages(ProviderMessagingDTO messages);
    Map<String, DomainMessage> bulkSendMessages(ProviderMessagingDTO messages);
    boolean cancelMessage(String resourceId);
}
