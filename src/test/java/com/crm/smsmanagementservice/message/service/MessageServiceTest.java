package com.crm.smsmanagementservice.message.service;

import com.crm.smsmanagementservice.conversation.ConversationInternalAPI;
import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.message.MessageDTO;
import com.crm.smsmanagementservice.message.persistence.MessageDocument;
import com.crm.smsmanagementservice.message.persistence.MessageMapper;
import com.crm.smsmanagementservice.message.persistence.MessageRepository;
import com.crm.smsmanagementservice.provider.MessagingProviderInternalAPI;
import com.crm.smsmanagementservice.provider.ProviderMessagingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessagingProviderInternalAPI messagingProvider;

    @Mock
    private ConversationInternalAPI conversationInternalAPI;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMessageById_Success() {
        String messageId = "msg-1";
        MessageDocument messageDocument = MessageDocument.builder().build();
        messageDocument.setId(messageId);
        MessageDTO messageDTO = MessageDTO.builder().build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageDocument));
        when(messageMapper.toDTO(messageDocument)).thenReturn(messageDTO);

        MessageDTO result = messageService.getMessageById(messageId);

        verify(messageRepository, times(1)).findById(messageId);
        verify(messageMapper, times(1)).toDTO(messageDocument);
        assertNotNull(result);
    }

    @Test
    void testGetMessageById_NotFound() {
        String messageId = "msg-1";

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> messageService.getMessageById(messageId));
        verify(messageRepository, times(1)).findById(messageId);
    }

    @Test
    void testGetMessageByParticipantId() {
        String userId = "user1";
        String contactId = "contact1";
        Pageable pageable = PageRequest.of(0, 10);
        MessageDocument messageDocument = MessageDocument.builder().build();
        List<MessageDocument> messageList = List.of(messageDocument);
        Page<MessageDocument> messagePage = new PageImpl<>(messageList, pageable, 1);
        MessageDTO messageDTO = MessageDTO.builder().build();

        when(messageRepository.findByUserIdAndContactIdAndStatus(userId, contactId, MessageStatus.DELIVERED, pageable))
                .thenReturn(messagePage);
        when(messageMapper.toDTO(messageDocument)).thenReturn(messageDTO);

        Page<MessageDTO> result = messageService.getMessageByParticipantId(userId, contactId, pageable);

        verify(messageRepository, times(1))
                .findByUserIdAndContactIdAndStatus(userId, contactId, MessageStatus.DELIVERED, pageable);
        verify(messageMapper, times(1)).toDTO(messageDocument);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testCreateMessage_Success() {
        MessageDTO messageDTO = MessageDTO.builder()
                .from("1234567890")
                .to("0987654321")
                .content("Hello")
                .contactId("contact1")
                .userId("user1")
                .build();
        List<MessageDTO> messageDTOList = List.of(messageDTO);
        ProviderMessagingDTO.MessageItemDTO messageItemDTO = ProviderMessagingDTO.MessageItemDTO.builder().build();
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItemDTOMap = new HashMap<>();
        messageItemDTOMap.put("user1contact1", messageItemDTO);
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg-1");

        Map<String, DomainMessage> response = Map.of("user1contact1", domainMessage);
        MessageDocument messageDocument = MessageDocument.builder().build();
        List<MessageDocument> messageDocumentList = List.of(messageDocument);

        when(messagingProvider.sendMessages(any(ProviderMessagingDTO.class))).thenReturn(response);
        when(messageMapper.toDocument(any(DomainMessage.class))).thenReturn(messageDocument);
        when(messageRepository.saveAll(anyList())).thenReturn(messageDocumentList);
        when(messageMapper.toDTO(any(MessageDocument.class))).thenReturn(messageDTO);

        List<MessageDTO> result = messageService.createMessage(messageDTOList);

        verify(messagingProvider, times(1)).sendMessages(any(ProviderMessagingDTO.class));
        verify(messageMapper, times(1)).toDocument(any(DomainMessage.class));
        verify(messageRepository, times(1)).saveAll(anyList());
        assertNotNull(result);
    }

    @Test
    void testCancelMessage_Success() {
        String messageId = "msg-1";
        MessageDocument messageDocument = MessageDocument.builder()
                .id(messageId)
                .resourceId("res-1")
                .status(MessageStatus.QUEUED)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageDocument));
        when(messagingProvider.cancelMessage(messageDocument.getResourceId())).thenReturn(true);
        when(messageRepository.save(any(MessageDocument.class))).thenReturn(messageDocument);
        when(messageMapper.toDTO(messageDocument)).thenReturn(MessageDTO.builder().build());

        MessageDTO result = messageService.cancelMessage(messageId);

        verify(messagingProvider, times(1)).cancelMessage(anyString());
        verify(messageRepository, times(1)).save(any(MessageDocument.class));
        assertNotNull(result);
    }

    @Test
    void testCancelMessage_AlreadyCancelled() {
        String messageId = "msg-1";
        MessageDocument messageDocument = MessageDocument.builder().build();
        messageDocument.setStatus(MessageStatus.CANCELLED);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageDocument));

        assertThrows(DomainException.class, () -> messageService.cancelMessage(messageId));
        verify(messageRepository, times(1)).findById(messageId);
    }

    @Test
    void testUpdateMessageStatus_Success() {
        String messageId = "msg-1";
        MessageDocument messageDocument = MessageDocument.builder().build();
        messageDocument.setStatus(MessageStatus.SENDING);

        when(messageRepository.findByResourceId(messageId)).thenReturn(Optional.of(messageDocument));

        messageService.updateMessageStatus(messageId, MessageStatus.DELIVERED, "Success", null);

        verify(messageRepository, times(1)).save(any(MessageDocument.class));
    }

    @Test
    void testUpdateMessage_DomainMessage() {
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg-1");
        when(domainMessage.getStatus()).thenReturn(MessageStatus.FAILED);
        when(domainMessage.getErrorMessage()).thenReturn(Optional.of("Error"));
        when(domainMessage.getErrorCode()).thenReturn(Optional.of("ERR123"));
        when(domainMessage.getRecipient()).thenReturn("recipient");
        when(domainMessage.getSender()).thenReturn(Optional.of("sender"));
        when(domainMessage.getPrice()).thenReturn(BigDecimal.valueOf(0.0));
        when(domainMessage.getCurrency()).thenReturn(Currency.getInstance("USD"));

        when(domainMessage.getId()).thenReturn("msg-1");
        MessageDocument messageDocument = MessageDocument.builder()
                .id("msg-1")
                .resourceId("msg-1")
                .status(MessageStatus.SENDING)
                .build();

        when(messageRepository.findByResourceId(anyString())).thenReturn(Optional.of(messageDocument));

        messageService.updateMessage(domainMessage);

        verify(messageRepository, times(1)).save(any(MessageDocument.class));
    }

    @Test
    void testCreateInboundMessage() {
        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg-1");
        when(domainMessage.getSender()).thenReturn(Optional.of("sender"));
        when(domainMessage.getStatus()).thenReturn(MessageStatus.RECEIVING);
        when(domainMessage.getRecipient()).thenReturn("recipient");
        MessageDocument messageDocument = MessageDocument.builder()
                .id("msg-1")
                .resourceId("msg-1")
                .userId("recipient")
                .contactId("sender")
                .conversationId("conv-1")
                .status(MessageStatus.RECEIVED)
                .build();

        when(messageMapper.toDocument(domainMessage)).thenReturn(messageDocument);
        when(messageRepository.findFirstByToAndDirectionOrderByCreatedDateDesc(anyString(), any()))
                .thenReturn(Optional.of(messageDocument));
        when(messageMapper.toDocument(domainMessage)).thenReturn(messageDocument);
        when(messageRepository.save(any(MessageDocument.class))).thenReturn(messageDocument);

        messageService.createInboundMessage(domainMessage);

        verify(messageRepository, times(1)).save(any(MessageDocument.class));
    }
}
