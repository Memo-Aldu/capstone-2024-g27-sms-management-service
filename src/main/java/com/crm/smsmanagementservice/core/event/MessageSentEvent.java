package com.crm.smsmanagementservice.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-18, Wednesday
 */
public class MessageSentEvent extends ApplicationEvent {
    public MessageSentEvent(Object source) {
        super(source);
    }
}
