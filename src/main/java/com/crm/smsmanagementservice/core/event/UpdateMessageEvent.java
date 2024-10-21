package com.crm.smsmanagementservice.core.event;

import com.crm.smsmanagementservice.core.enums.MessageStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-18, Thursday
 */

@Getter
public class UpdateMessageEvent extends ApplicationEvent {
    private final String messageId;
    private final String accountId;
    private final String serviceId;
    private final String errorCode;
    private final String errorMessage;
    private final MessageStatus messageStatus;

    public UpdateMessageEvent(Object source, String messageId, String accountId,
                              String serviceId, String errorCode, String errorMessage,
                              MessageStatus messageStatus) {
        super(source);
        this.messageId = messageId;
        this.accountId = accountId;
        this.serviceId = serviceId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.messageStatus = messageStatus;
    }
}
