package com.crm.smsmanagementservice.message.service;

import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.core.event.DeliveredMessageEvent;
import com.crm.smsmanagementservice.core.event.InboundMessageEvent;
import com.crm.smsmanagementservice.core.event.UpdateMessageEvent;
import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.message.MessageInternalAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessageEventListenerTest {

    @Mock
    private MessageInternalAPI messageInternalAPI;

    @InjectMocks
    private MessageEventListener messageEventListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleIncomingMessageEvent() {
        // Given
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg-1");
        InboundMessageEvent inboundMessageEvent = new InboundMessageEvent(this, domainMessage);

        // When
        messageEventListener.handleIncomingMessageEvent(inboundMessageEvent);

        // Then
        verify(messageInternalAPI, times(1)).createInboundMessage(eq(domainMessage));
    }

    @Test
    void testHandleUpdateMessageEvent() {
        // Given
        String messageId = "msg-1";
        String accountId = "acc-1";
        String serviceId = "svc-1";
        String errorMessage = "Some error";
        String errorCode = "ERR123";
        MessageStatus status = MessageStatus.FAILED;
        UpdateMessageEvent updateMessageEvent = new UpdateMessageEvent(
                this, messageId, accountId, serviceId,
                errorCode, errorMessage, status
        );

        // When
        messageEventListener.handleUpdateMessageEvent(updateMessageEvent);

        // Then
        verify(messageInternalAPI, times(1)).updateMessageStatus(eq(messageId), eq(status), eq(errorMessage), eq(errorCode));
    }

    @Test
    void testHandleDeliveredMessageEvent() {
        // Given
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg-1");
        DeliveredMessageEvent deliveredMessageEvent = new DeliveredMessageEvent(this, domainMessage);

        // When
        messageEventListener.handleDeliveredMessageEvent(deliveredMessageEvent);

        // Then
        verify(messageInternalAPI, times(1)).updateMessage(eq(domainMessage));
    }
}
