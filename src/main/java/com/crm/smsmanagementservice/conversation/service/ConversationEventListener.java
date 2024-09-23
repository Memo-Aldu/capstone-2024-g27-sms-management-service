package com.crm.smsmanagementservice.conversation.service;

import com.crm.smsmanagementservice.conversation.ConversationInternalAPI;
import com.crm.smsmanagementservice.core.event.RequestConversationEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-13, Friday
 */
@Component
@AllArgsConstructor
@Slf4j(topic = "CONVERSATION_LISTENER")
public class ConversationEventListener {
    private final ConversationInternalAPI conversationInternalAPI;


    @ApplicationModuleListener
    public void handleRequestConversationEvent(RequestConversationEvent event) {
        log.info("Handling request conversation: {}", event.getMessageId());
    }
}
