package com.crm.smsmanagementservice.message.service;

import com.crm.smsmanagementservice.core.event.DeliveredMessageEvent;
import com.crm.smsmanagementservice.core.event.InboundMessageEvent;
import com.crm.smsmanagementservice.core.event.UpdateMessageEvent;
import com.crm.smsmanagementservice.message.MessageInternalAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-18, Thursday
 */

@Component @AllArgsConstructor @Slf4j(topic = "INBOUND_MESSAGE_LISTENER")
public class MessageEventListener {
    private final MessageInternalAPI messageInternalAPI;

    @ApplicationModuleListener
    public void handleIncomingMessageEvent(InboundMessageEvent event) {
        log.info("Handling incoming message: {}", event.getMessage());
        messageInternalAPI.createInboundMessage(event.getMessage());
    }

    @ApplicationModuleListener
    public void handleUpdateMessageEvent(UpdateMessageEvent event) {
        log.info("Handling update message: {}", event.getMessageId());
        messageInternalAPI.updateMessageStatus(event.getMessageId(), event.getMessageStatus(), event.getErrorMessage(), event.getErrorCode());
    }

    @ApplicationModuleListener
    public void handleDeliveredMessageEvent(DeliveredMessageEvent event) {
        log.info("Handling update message: {}", event.getMessage().getId());
        messageInternalAPI.updateMessage(event.getMessage());
    }
}
