package com.crm.smsmanagementservice.provider;

import com.crm.smsmanagementservice.provider.web.InboundMessageDTO;
import com.crm.smsmanagementservice.provider.web.MessageStatusUpdateDTO;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
public interface MessagingProviderExternalAPI {
    void handleIncomingMessage(InboundMessageDTO message);
    void handleIncomingMessageStatusUpdate(MessageStatusUpdateDTO message);
}
