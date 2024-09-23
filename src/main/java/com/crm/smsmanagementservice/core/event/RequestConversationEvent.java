package com.crm.smsmanagementservice.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-12, Thursday
 */
@Getter
public class RequestConversationEvent extends ApplicationEvent {
    private final String messageId;
    private final String from;
    private final String to;

    public RequestConversationEvent(Object source, String messageId, String from, String to) {
        super(source);
        this.messageId = messageId;
        this.from = from;
        this.to = to;
    }
}
