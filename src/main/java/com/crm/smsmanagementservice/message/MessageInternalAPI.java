package com.crm.smsmanagementservice.message;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageStatus;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-20, Saturday
 */
public interface MessageInternalAPI {
    void updateMessage(DomainMessage message);
    void createInboundMessage(DomainMessage message);
    void updateMessageStatus(String messageId, MessageStatus status, String errorMessage, String errorCode);
}
