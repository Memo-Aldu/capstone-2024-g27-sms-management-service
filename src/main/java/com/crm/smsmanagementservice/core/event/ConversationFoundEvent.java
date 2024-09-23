package com.crm.smsmanagementservice.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-12, Thursday
 */
public class ConversationFoundEvent extends ApplicationEvent {
    String userId;
    String contactId;
    String messageId;

    public ConversationFoundEvent(Object source, String userId, String contactId, String messageId) {
        super(source);
        this.userId = userId;
        this.contactId = contactId;
        this.messageId = messageId;
    }
}
