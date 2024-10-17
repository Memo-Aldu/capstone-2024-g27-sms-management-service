package com.crm.smsmanagementservice.provider.service;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.core.event.DeliveredMessageEvent;
import com.crm.smsmanagementservice.core.event.InboundMessageEvent;
import com.crm.smsmanagementservice.core.event.UpdateMessageEvent;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.provider.ProviderMessagingDTO;
import com.crm.smsmanagementservice.provider.web.InboundMessageDTO;
import com.crm.smsmanagementservice.provider.web.MessageStatusUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageProviderServiceTest {

    @Mock
    private MessagingClient messagingClient;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private MessageProviderService messageProviderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessages_SMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .media(Collections.emptyList())
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.sendSMSFromNumber(anyString(), anyString(), anyString()))
                .thenReturn(domainMessage);

        when(messagingClient.sendSMSFromNumber(anyString(), anyString(), anyString()))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.sendMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .sendSMSFromNumber(anyString(), anyString(), anyString());
    }

    @Test
    void testSendMessages_MMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .media(Collections.singletonList("media"))
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.sendMMSFromNumber(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.sendMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .sendMMSFromNumber(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    void testScheduleMessages_SMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .scheduledDate(ZonedDateTime.now())
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.scheduleSMS(anyString(), anyString(), any(ZonedDateTime.class)))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.scheduleMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .scheduleSMS(anyString(), anyString(), any(ZonedDateTime.class));
    }

    @Test
    void testScheduleMessages_MMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .media(Collections.singletonList("media"))
                .scheduledDate(ZonedDateTime.now())
                .build();

        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.scheduleMMS(anyString(), anyString(), anyList(), any(ZonedDateTime.class)))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.scheduleMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .scheduleMMS(anyString(), anyString(), anyList(), any(ZonedDateTime.class));
    }

    @Test
    void testScheduleMessagesSMSWithNullDate() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .build();

        assertThrows(DomainException.class, () -> messageProviderService.scheduleMessages(messagingDTO));
    }

    @Test
    void testScheduleMessagesSMSWithNullDto() {
        assertThrows(NullPointerException.class, () -> messageProviderService.scheduleMessages(null));
    }

    @Test
    void testBulkSendMessages_SMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.sendSMSFromService(anyString(), anyString()))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.bulkSendMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .sendSMSFromService(anyString(), anyString());
    }

    @Test
    void testBulkSendMessages_MMS() {
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItems = new HashMap<>();
        messageItems.put("message-id", new ProviderMessagingDTO.MessageItemDTO("content", "recipient", "sender"));
        ProviderMessagingDTO messagingDTO = ProviderMessagingDTO.builder()
                .messageItems(messageItems)
                .media(Collections.singletonList("media"))
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.sendMMSFromService(anyString(), anyString(), anyList()))
                .thenReturn(domainMessage);

        Map<String, DomainMessage> result = messageProviderService.bulkSendMessages(messagingDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messagingClient, times(1))
                .sendMMSFromService(anyString(), anyString(), anyList());
    }

    @Test
    void testCancelMessage_Success() {
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getStatus()).thenReturn(MessageStatus.CANCELED);


        when(messagingClient.cancelMessage(anyString()))
                .thenReturn(domainMessage);

        boolean result = messageProviderService.cancelMessage("test-resource-id");

        assertTrue(result);
        verify(messagingClient, times(1)).cancelMessage(anyString());
    }


    @Test
    void testCancelMessageNull_Success() {
        assertThrows(NullPointerException.class, () -> messageProviderService.cancelMessage(null));
    }

    @Test
    void testHandleIncomingMessage() {
        InboundMessageDTO messageDTO = InboundMessageDTO.builder()
                .messageId("message-id")
                .messageStatus("DELIVERED")
                .from("sender")
                .to("recipient")
                .body("content")
                .build();
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.fetchMessageById(anyString()))
                .thenReturn(domainMessage);

        messageProviderService.handleIncomingMessage(messageDTO);

        ArgumentCaptor<InboundMessageEvent> eventCaptor = ArgumentCaptor.forClass(InboundMessageEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InboundMessageEvent event = eventCaptor.getValue();
        assertEquals(domainMessage, event.getMessage());
    }

    @Test
    void testHandleIncomingMessageStatusUpdate_Delivered() {
        MessageStatusUpdateDTO messageDTO = new MessageStatusUpdateDTO(
                "DELIVERED", "account-id", "service-id", "DELIVERED", null, null);
        DomainMessage domainMessage = mock(DomainMessage.class);

        when(messagingClient.fetchMessageById(anyString()))
                .thenReturn(domainMessage);

        messageProviderService.handleIncomingMessageStatusUpdate(messageDTO);

        ArgumentCaptor<DeliveredMessageEvent> eventCaptor = ArgumentCaptor.forClass(DeliveredMessageEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        DeliveredMessageEvent event = eventCaptor.getValue();
        assertEquals(domainMessage, event.getMessage());
    }

    @Test
    void testHandleIncomingMessageStatusUpdate_Update() {
        MessageStatusUpdateDTO messageDTO = new MessageStatusUpdateDTO(
                "FAILED", "message-id", "service-id", "FAILED", "500", "Error occurred");
        messageProviderService.handleIncomingMessageStatusUpdate(messageDTO);

        ArgumentCaptor<UpdateMessageEvent> eventCaptor = ArgumentCaptor.forClass(UpdateMessageEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        UpdateMessageEvent event = eventCaptor.getValue();
        assertEquals("message-id", event.getMessageId());
        assertEquals("500", event.getErrorCode());
        assertEquals("Error occurred", event.getErrorMessage());
    }

}
