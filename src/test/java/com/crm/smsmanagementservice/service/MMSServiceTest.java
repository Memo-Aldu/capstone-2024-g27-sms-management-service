package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.mapper.MMSMapper;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import com.crm.smsmanagementservice.service.mms.MMSService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/28/2024, Thursday
 */
@ExtendWith(MockitoExtension.class)
public class MMSServiceTest {
  @Mock private MMSMapper mmsMapper;
  @Mock private IConversationService conversationService;
  @Mock private IMessageService messageService;
  @Mock private IMessagingProviderService messagingProvider;
  @InjectMocks private MMSService mmsService;

  @Test
  public void whenSendMMS_thenSuccess() {
    MMSSendRequestDto request =
        new MMSSendRequestDto(
            "1234567890",
            "1234567890",
            "Hello, World!",
            List.of("http://example.com/image.jpg"),
            "CONVERSATION_ID");
    MessageResponseDto messageResponseDto =
        MessageResponseDto.builder()
            .id("MESSAGE_ID")
            .messageContent("Hello, World!")
            .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
            .conversationId("CONVERSATION_ID")
            .build();

    ConversationResponseDto conversationDto =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("1234567890")
            .build();
    IMessageWrapper mockMessage = mock(IMessageWrapper.class);

    when(messagingProvider.sendMMSFromNumber(
            request.recipient(), request.sender(), request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage);

    when(conversationService.getOrCreateConversation(request.sender(), request.recipient()))
        .thenReturn(conversationDto);

    when(messageService.saveMessage(mockMessage, conversationDto.id()))
        .thenReturn(messageResponseDto);

    when(mmsMapper.toMMSSendResponseDto(messageResponseDto))
        .thenReturn(
            new MMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID"));

    MMSSendResponseDto responseDto = mmsService.sendMMS(request);

    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals(MessageStatus.DELIVERED, responseDto.status());
    assertEquals("CONVERSATION_ID", responseDto.conversationId());
  }

  @Test
  public void whenScheduleMMS_thenSuccess() {
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    MMSScheduleRequestDto request =
        new MMSScheduleRequestDto(
            "1234567890",
            "2234567890",
            "Hello, World!",
            scheduledTime,
            List.of("http://example.com/image.jpg"),
            "CONVERSATION_ID");

    MessageResponseDto messageResponseDto =
        MessageResponseDto.builder()
            .id("MESSAGE_ID")
            .messageContent("Hello, World!")
            .scheduledTime(scheduledTime)
            .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
            .conversationId("CONVERSATION_ID")
            .build();

    ConversationResponseDto conversationDto =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("2234567890")
            .build();

    IMessageWrapper mockMessage = mock(IMessageWrapper.class);

    when(messagingProvider.scheduleMMS(
            request.recipient(), request.messageContent(), request.mediaUrls(), scheduledTime))
        .thenReturn(mockMessage);

    when(conversationService.getOrCreateConversation(request.sender(), request.recipient()))
        .thenReturn(conversationDto);

    when(messageService.saveMessage(mockMessage, scheduledTime, conversationDto.id()))
        .thenReturn(messageResponseDto);

    when(mmsMapper.toMMSScheduleResponseDto(messageResponseDto))
        .thenReturn(
            new MMSScheduleResponseDto(
                "MESSAGE_ID", MessageStatus.SCHEDULED, scheduledTime, "CONVERSATION_ID"));

    MMSScheduleResponseDto responseDto = mmsService.scheduleMMS(request);
    assert responseDto != null;
    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals("CONVERSATION_ID", responseDto.conversationId());
    assertEquals(MessageStatus.SCHEDULED, responseDto.status());
    assertEquals(scheduledTime, responseDto.scheduledTime());
  }

  @Test
  public void whenSendBulkMMS_thenSuccess() {
    MMSBulkSendRequestDto request =
        new MMSBulkSendRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567893", "1234567891"), List.of("http://example.com/image.jpg"));

    MessageResponseDto messageResponseDto1 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
                .conversationId("CONVERSATION_ID_1")
                .build();

        MessageResponseDto messageResponseDto2 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
                .conversationId("CONVERSATION_ID_2")
                .build();

    ConversationResponseDto conversationDto1 =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID_1")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("1234567893")
            .build();
    ConversationResponseDto conversationDto2 =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID_2")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("1234567891")
            .build();

    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);

    when(messagingProvider.sendMMSFromService(
            "1234567893", request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage1);
    when(messagingProvider.sendMMSFromService(
            "1234567891", request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage2);

    when(conversationService.getOrCreateConversation(request.sender(), "1234567893")).thenReturn(conversationDto1);
    when(conversationService.getOrCreateConversation(request.sender(), "1234567891")).thenReturn(conversationDto2);

    when(messageService.saveMessage(mockMessage1, conversationDto1.id())).thenReturn(messageResponseDto1);
    when(messageService.saveMessage(mockMessage2, conversationDto2.id())).thenReturn(messageResponseDto2);

    when(mmsMapper.toMMSSendResponseDto(messageResponseDto1))
        .thenReturn(new MMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID_1"));
    when(mmsMapper.toMMSSendResponseDto(messageResponseDto2))
        .thenReturn(new MMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID_2"));

    MMSBulkSendResponseDto response = mmsService.sendBulkMMS(request);
    assert response != null;
    assertEquals(2, response.messages().size());
    assertEquals(MessageStatus.DELIVERED, response.messages().getFirst().status());
    assertEquals(MessageStatus.DELIVERED, response.messages().getLast().status());
  }

  @Test
  public void whenScheduleBulkMMS_thenSuccess() {
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    MMSBulkScheduleRequestDto request =
        new MMSBulkScheduleRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567893", "1234567891"), scheduledTime, List.of("http://example.com/image.jpg"));

    MessageResponseDto messageResponseDto1 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
                .conversationId("CONVERSATION_ID_1")
                .build();

        MessageResponseDto messageResponseDto2 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .mediaUrls(Map.of("1", "http://example.com/image.jpg"))
                .conversationId("CONVERSATION_ID_2")
                .build();

    ConversationResponseDto conversationDto1 =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID_1")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("1234567893")
            .build();
    ConversationResponseDto conversationDto2 =
        ConversationResponseDto.builder()
            .id("CONVERSATION_ID_2")
            .createdTime(ZonedDateTime.now())
            .sender("1234567890")
            .recipient("1234567891")
            .build();

    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);

    when(messagingProvider.scheduleMMS(
            "1234567893", request.messageContent(), request.mediaUrls(), scheduledTime))
        .thenReturn(mockMessage1);
    when(messagingProvider.scheduleMMS(
            "1234567891", request.messageContent(), request.mediaUrls(), scheduledTime))
        .thenReturn(mockMessage2);

    when(conversationService.getOrCreateConversation(request.sender(), "1234567893")).thenReturn(conversationDto1);
    when(conversationService.getOrCreateConversation(request.sender(), "1234567891")).thenReturn(conversationDto2);

    when(messageService.saveMessage(mockMessage1, scheduledTime, conversationDto1.id())).thenReturn(messageResponseDto1);
    when(messageService.saveMessage(mockMessage2, scheduledTime, conversationDto2.id())).thenReturn(messageResponseDto2);

    when(mmsMapper.toMMSSendResponseDto(messageResponseDto1))
        .thenReturn(new MMSSendResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, "CONVERSATION_ID_1"));
    when(mmsMapper.toMMSSendResponseDto(messageResponseDto2))
        .thenReturn(new MMSSendResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, "CONVERSATION_ID_2"));

    MMSBulkScheduleResponseDto response = mmsService.scheduleBulkMMS(request);
    assert response != null;
    assertEquals(2, response.messages().size());
    assertEquals(MessageStatus.SCHEDULED, response.messages().getFirst().status());
    assertEquals(MessageStatus.SCHEDULED, response.messages().getLast().status());
  }
}
