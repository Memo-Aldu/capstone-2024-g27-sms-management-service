package com.crm.smsmanagementservice.core.event;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-18, Thursday
 */

@Getter
public class DeliveredMessageEvent extends ApplicationEvent {
    private final DomainMessage message;

    public DeliveredMessageEvent(Object source, DomainMessage message) {
        super(source);
        this.message = message;
    }
}
