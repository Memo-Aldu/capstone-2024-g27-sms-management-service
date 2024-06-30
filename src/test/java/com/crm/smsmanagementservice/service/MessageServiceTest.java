package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.MessageMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.message.MessageService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock private MessageRepository messageRepository;

    @Mock private MessageMapper messageMapper;

    @InjectMocks private MessageService messageService;

    private MessageDocument messageDocument;
    private MessageResponseDto messageResponseDto;

    @BeforeEach
    public void setUp() {
        messageDocument =
                MessageDocument.builder()
                        .id("MESSAGE_ID")
                        .conversationId("CONVERSATION_ID")
                        .sender("SENDER")
                        .recipient("RECIPIENT")
                        .messageContent("Hello, World!")
                        .status(MessageStatus.SENT)
                        .build();

        messageResponseDto =
                MessageResponseDto.builder()
                        .id("MESSAGE_ID")
                        .conversationId("CONVERSATION_ID")
                        .status(MessageStatus.SENT)
                        .messageContent("Hello, World!")
                        .deliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()))
                        .build();
    }

    @Test
    public void testSaveMessage() {
        when(messageRepository.save(any(MessageDocument.class))).thenReturn(messageDocument);
        when(messageMapper.toDocument(any(IMessageWrapper.class))).thenReturn(messageDocument);
        when(messageMapper.toResponseDto(any(MessageDocument.class))).thenReturn(messageResponseDto);

        IMessageWrapper message = mock(IMessageWrapper.class);

        MessageResponseDto response = messageService.saveMessage(message, "CONVERSATION_ID");

        assertNotNull(response);
        assertEquals("MESSAGE_ID", response.id());
    }

    @Test
    public void testSaveScheduledMessage() {
        // given
        ZonedDateTime scheduledTime = ZonedDateTime.now().plusMinutes(5);

        when(messageRepository.save(any(MessageDocument.class))).thenReturn(messageDocument);
        when(messageMapper.toDocument(any(IMessageWrapper.class))).thenReturn(messageDocument);
        when(messageMapper.toResponseDto(any(MessageDocument.class))).thenReturn(messageResponseDto);

        IMessageWrapper message = mock(IMessageWrapper.class);

        MessageResponseDto response = messageService.saveMessage(message, scheduledTime, "CONVERSATION_ID");

        assertNotNull(response);
        assertEquals("MESSAGE_ID", response.id());
        assertEquals(scheduledTime, messageDocument.getScheduledTime());
    }

    @Test
    public void testUpdateMessageStatus() {
        messageDocument.setStatus(MessageStatus.DELIVERED); // Update the status in the mock document
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(messageDocument));
        when(messageMapper.toResponseDto(any(MessageDocument.class)))
                .thenAnswer(
                        invocation -> {
                            MessageDocument savedMessage = invocation.getArgument(0);
                            return MessageResponseDto.builder()
                                    .id(savedMessage.getId())
                                    .conversationId(savedMessage.getConversationId())
                                    .status(savedMessage.getStatus())
                                    .messageContent(savedMessage.getMessageContent())
                                    .deliveredTime(savedMessage.getDeliveredTime())
                                    .build();
                        });
        MessageResponseDto response =
                messageService.updateMessageStatus("MESSAGE_ID", MessageStatus.DELIVERED, null, null);

        assertNotNull(response);
        assertEquals(MessageStatus.DELIVERED, response.status());
    }

    @Test
    public void testGetMessageById() {
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(messageDocument));
        when(messageMapper.toResponseDto(any(MessageDocument.class))).thenReturn(messageResponseDto);

        MessageResponseDto response = messageService.getMessageById("MESSAGE_ID");

        assertNotNull(response);
        assertEquals("MESSAGE_ID", response.id());
    }

    @Test
    public void testGetAllMessagesByStatus() {
        when(messageRepository.findAllByStatus(any(List.class)))
                .thenReturn(Collections.singletonList(messageDocument));
        when(messageMapper.toResponseDtoList(any(List.class)))
                .thenReturn(Collections.singletonList(messageResponseDto));

        List<MessageResponseDto> responses =
                messageService.getAllMessagesByStatus(Collections.singletonList(MessageStatus.SENT));

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("MESSAGE_ID", responses.getFirst().id());
    }

    @Test
    public void testGetMessagesByConversationId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MessageDocument> page =
                new PageImpl<>(Collections.singletonList(messageDocument), pageable, 1);
        when(messageRepository.findByConversationId(anyString(), any(Pageable.class))).thenReturn(page);
        when(messageMapper.toResponseDto(any(MessageDocument.class))).thenReturn(messageResponseDto);

        Page<MessageResponseDto> responses =
                messageService.getMessagesByConversationId("CONVERSATION_ID", pageable);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals("MESSAGE_ID", responses.getContent().getFirst().id());
    }
}
