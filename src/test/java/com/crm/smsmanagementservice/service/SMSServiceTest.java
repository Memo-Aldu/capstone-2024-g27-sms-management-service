package com.crm.smsmanagementservice.service;


import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.mapper.SMSMapper;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import com.crm.smsmanagementservice.service.sms.SMSService;
import com.crm.smsmanagementservice.enums.MessageStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class SMSServiceTest {
  @Mock private SMSMapper smsMapper;
  @Mock private IConversationService conversationService;
  @Mock private IMessageService messageService;
  @Mock private IMessagingProviderService messagingProvider;
  @InjectMocks private SMSService smsService;

  @Test
  public void whenSendSMS_thenSuccess() {
    SMSSendRequestDto request =
        new SMSSendRequestDto(
            "1234567890",
            "1234567890",
            "Hello, World!",
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

    when(messagingProvider.sendSMSFromNumber(
            request.recipient(), request.sender(), request.messageContent()))
        .thenReturn(mockMessage);

    when(conversationService.getOrCreateConversation(request.sender(), request.recipient()))
        .thenReturn(conversationDto);

    when(messageService.saveMessage(mockMessage, conversationDto.id()))
        .thenReturn(messageResponseDto);

    when(smsMapper.toSMSSendResponseDto(messageResponseDto))
        .thenReturn(
            new SMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID"));

    SMSSendResponseDto responseDto = smsService.sendSMS(request);

    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals(MessageStatus.DELIVERED, responseDto.status());
    assertEquals("CONVERSATION_ID", responseDto.conversationId());
  }

  @Test
  public void whenScheduleSMS_thenSuccess() {
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSScheduleRequestDto request =
        new SMSScheduleRequestDto(
            "1234567890",
            "2234567890",
            "Hello, World!",
            scheduledTime,
            "CONVERSATION_ID");

    MessageResponseDto messageResponseDto =
        MessageResponseDto.builder()
            .id("MESSAGE_ID")
            .messageContent("Hello, World!")
            .scheduledTime(scheduledTime)
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

    when(messagingProvider.scheduleSMS(
            request.recipient(), request.messageContent(), scheduledTime))
        .thenReturn(mockMessage);

    when(conversationService.getOrCreateConversation(request.sender(), request.recipient()))
        .thenReturn(conversationDto);

    when(messageService.saveMessage(mockMessage, scheduledTime ,conversationDto.id()))
        .thenReturn(messageResponseDto);

    when(smsMapper.toSMSScheduleResponseDto(messageResponseDto))
        .thenReturn(
            new SMSScheduleResponseDto(
                "MESSAGE_ID", MessageStatus.SCHEDULED, scheduledTime, "CONVERSATION_ID"));

    SMSScheduleResponseDto responseDto = smsService.scheduleSMS(request);
    assert responseDto != null;
    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals("CONVERSATION_ID", responseDto.conversationId());
    assertEquals(MessageStatus.SCHEDULED, responseDto.status());
    assertEquals(scheduledTime, responseDto.scheduledTime());
  }

  @Test
  public void whenSendBulkSMS_thenSuccess() {
    SMSBulkSendRequestDto request =
        new SMSBulkSendRequestDto("1234567890",
                "Hello, World!", List.of("1234567893", "1234567891"));

    MessageResponseDto messageResponseDto1 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .conversationId("CONVERSATION_ID_1")
                .build();

        MessageResponseDto messageResponseDto2 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
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

    when(messagingProvider.sendSMSFromService(
            "1234567893", request.messageContent()))
        .thenReturn(mockMessage1);
    when(messagingProvider.sendSMSFromService(
            "1234567891", request.messageContent()))
        .thenReturn(mockMessage2);

    when(conversationService.getOrCreateConversation(request.sender(), "1234567893")).thenReturn(conversationDto1);
    when(conversationService.getOrCreateConversation(request.sender(), "1234567891")).thenReturn(conversationDto2);

    when(messageService.saveMessage(mockMessage1, conversationDto1.id())).thenReturn(messageResponseDto1);
    when(messageService.saveMessage(mockMessage2, conversationDto2.id())).thenReturn(messageResponseDto2);

    when(smsMapper.toSMSSendResponseDto(messageResponseDto1))
        .thenReturn(new SMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID_1"));
    when(smsMapper.toSMSSendResponseDto(messageResponseDto2))
        .thenReturn(new SMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "CONVERSATION_ID_2"));

    SMSBulkSendResponseDto response = smsService.sendBulkSMS(request);
    assert response != null;
    assertEquals(2, response.messages().size());
    assertEquals(MessageStatus.DELIVERED, response.messages().getFirst().status());
    assertEquals(MessageStatus.DELIVERED, response.messages().getLast().status());
  }

  @Test
  public void whenScheduleBulkSMS_thenSuccess() {
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSBulkScheduleRequestDto request =
        new SMSBulkScheduleRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567893", "1234567891"), scheduledTime);

    MessageResponseDto messageResponseDto1 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
                .conversationId("CONVERSATION_ID_1")
                .build();

        MessageResponseDto messageResponseDto2 =
        MessageResponseDto.builder()
                .id("MESSAGE_ID")
                .status(MessageStatus.DELIVERED)
                .messageContent("Hello, World!")
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

    when(messagingProvider.scheduleSMS(
            "1234567893", request.messageContent(), scheduledTime))
        .thenReturn(mockMessage1);
    when(messagingProvider.scheduleSMS(
            "1234567891", request.messageContent(), scheduledTime))
        .thenReturn(mockMessage2);

    when(conversationService.getOrCreateConversation(request.sender(), "1234567893")).thenReturn(conversationDto1);
    when(conversationService.getOrCreateConversation(request.sender(), "1234567891")).thenReturn(conversationDto2);

    when(messageService.saveMessage(mockMessage1, scheduledTime, conversationDto1.id())).thenReturn(messageResponseDto1);
    when(messageService.saveMessage(mockMessage2, scheduledTime, conversationDto2.id())).thenReturn(messageResponseDto2);

    when(smsMapper.toSMSSendResponseDto(messageResponseDto1))
        .thenReturn(new SMSSendResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, "CONVERSATION_ID_1"));
    when(smsMapper.toSMSSendResponseDto(messageResponseDto2))
        .thenReturn(new SMSSendResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, "CONVERSATION_ID_2"));

    SMSBulkScheduleResponseDto response = smsService.scheduleBulkSMS(request);
    assert response != null;
    assertEquals(2, response.messages().size());
    assertEquals(MessageStatus.SCHEDULED, response.messages().getFirst().status());
    assertEquals(MessageStatus.SCHEDULED, response.messages().getLast().status());
  }
}
