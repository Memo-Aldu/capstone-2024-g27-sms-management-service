package com.crm.smsmanagementservice.core.event;

import org.springframework.context.ApplicationEvent;

import java.time.ZonedDateTime;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-18, Wednesday
 */
public class UpdateConversationEvent extends ApplicationEvent {
    String conversationId;
    String messagePreview;
    ZonedDateTime lastMessageTime;
    public UpdateConversationEvent(Object source, String conversationId, String messagePreview, ZonedDateTime lastMessageTime) {
        super(source);
        this.conversationId = conversationId;
        this.messagePreview = messagePreview;
        this.lastMessageTime = lastMessageTime;
    }
}
