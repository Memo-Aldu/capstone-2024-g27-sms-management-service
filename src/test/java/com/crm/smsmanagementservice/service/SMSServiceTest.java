package com.crm.smsmanagementservice.service;


import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.DtoMapper;
import com.crm.smsmanagementservice.mapper.DocumentMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.service.message.IMessagingService;
import com.crm.smsmanagementservice.service.sms.SMSService;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.twilio.dto.TwilioStatusCallbackDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class SMSServiceTest {
  @Mock private MessageRepository smsRepository;
  @Mock private DocumentMapper messageDocumentMapper;
  @Mock private DtoMapper dtoDocumentMapper;
  @Mock private IMessagingService twilioService;
  @InjectMocks private SMSService smsService;
  @Captor private ArgumentCaptor<MessageDocument> documentCaptor;


  @Test
  public void whenPollSMSStatus_thenDocumentStatusUpdated() {
    // given
    MessageDocument document =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .status(MessageStatus.QUEUED)
            .serviceSid("someServiceSid")
            .build();
    List<MessageStatus> NON_TERMINAL_STATUSES =
        List.of(
            MessageStatus.QUEUED,
            MessageStatus.SENT,
            MessageStatus.ACCEPTED,
            MessageStatus.SCHEDULED);
    List<MessageDocument> nonTerminalMessages = List.of(document);
    when(smsRepository.findAllByStatus(NON_TERMINAL_STATUSES)).thenReturn(nonTerminalMessages);

    IMessageWrapper message = mock(IMessageWrapper.class);
    when(twilioService.pollMessageStatus()).thenReturn(true);
    when(twilioService.fetchMessageById("someId")).thenReturn(message);
    when(message.getStatus()).thenReturn(MessageStatus.DELIVERED);

    // when
    smsService.pollSMSStatus();

    // then
    verify(smsRepository).save(documentCaptor.capture());
    MessageDocument capturedDocument = documentCaptor.getValue();
    assertEquals(MessageStatus.DELIVERED, capturedDocument.getStatus());
  }

    @Test
  public void whenPollSMSStatusFailed_thenDocumentStatusUpdated() {
    // given
    MessageDocument document =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .status(MessageStatus.QUEUED)
            .serviceSid("someServiceSid")
            .build();
    List<MessageStatus> NON_TERMINAL_STATUSES =
        List.of(
            MessageStatus.QUEUED,
            MessageStatus.SENT,
            MessageStatus.ACCEPTED,
            MessageStatus.SCHEDULED);
    List<MessageDocument> nonTerminalMessages = List.of(document);
    when(smsRepository.findAllByStatus(NON_TERMINAL_STATUSES)).thenReturn(nonTerminalMessages);

    IMessageWrapper message = mock(IMessageWrapper.class);
    when(twilioService.pollMessageStatus()).thenReturn(true);
    when(twilioService.fetchMessageById("someId")).thenReturn(message);
    when(message.getStatus()).thenReturn(MessageStatus.FAILED);
    when(message.getErrorCode()).thenReturn(Optional.of("errorCode"));
    when(message.getErrorMessage()).thenReturn(Optional.of("errorMessage"));

    // when
    smsService.pollSMSStatus();

    // then
    verify(smsRepository).save(documentCaptor.capture());
    MessageDocument capturedDocument = documentCaptor.getValue();
    assertEquals(MessageStatus.FAILED, capturedDocument.getStatus());
    assertEquals("errorCode", capturedDocument.getErrorCode());
    assertEquals("errorMessage", capturedDocument.getErrorMessage());
  }

  @Test
  public void whenSendSMS_thenSuccess() {
    // given
    SMSSendRequestDto requestDto = new SMSSendRequestDto("sender", "recipient", "messageContent");
    MessageDocument savedDocument =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();
    SMSSendResponseDto expectedResponseDto = new SMSSendResponseDto("someSid", MessageStatus.SENT);

    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.sendSMSFromNumber(requestDto.recipient(), requestDto.sender(), requestDto.messageContent()))
        .thenReturn(mockMessage);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(savedDocument);

    when(dtoDocumentMapper.toSMSSendResponseDto(any(MessageDocument.class)))
        .thenReturn(expectedResponseDto);

    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument);

    // when
    SMSSendResponseDto actualResponseDto = smsService.sendSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(expectedResponseDto, actualResponseDto);
    verify(twilioService, times(1))
        .sendSMSFromNumber(requestDto.recipient(), requestDto.sender(), requestDto.messageContent());
    verify(smsRepository, times(1)).save(savedDocument);
  }

  @Test
  public void whenScheduledSMS_thenSuccess() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSScheduleRequestDto requestDto =
        new SMSScheduleRequestDto("sender", "recipient", "messageContent", scheduledTime);
    MessageDocument savedDocument =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .serviceSid("someSid")
            .build();
    SMSScheduleResponseDto expectedResponseDto =
        new SMSScheduleResponseDto("someSid", MessageStatus.SCHEDULED, scheduledTime);
    IMessageWrapper mockMessage = mock(IMessageWrapper.class);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(savedDocument);

    when(dtoDocumentMapper.toSMSScheduleResponseDto(any(MessageDocument.class)))
        .thenReturn(expectedResponseDto);

    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument);
    when(twilioService.scheduleSMS(anyString(), anyString(), eq(scheduledTime)))
        .thenReturn(mockMessage);

    // when
    SMSScheduleResponseDto actualResponseDto = smsService.scheduleSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(expectedResponseDto, actualResponseDto);
    verify(twilioService).scheduleSMS(anyString(), anyString(), eq(scheduledTime));
    verify(smsRepository, times(1)).save(savedDocument);
  }

  @Test
  public void whenSendBulkSMS_thenSuccess() {
    // given
    SMSBulkSendRequestDto requestDto =
        new SMSBulkSendRequestDto("sender", "messageContent", List.of("recipient1", "recipient2"));
    MessageDocument savedDocument1 =
        MessageDocument.builder()
            .id("someId1")
            .recipient("recipient1")
            .messageContent("messageContent")
            .build();
    MessageDocument savedDocument2 =
        MessageDocument.builder()
            .id("someId2")
            .recipient("recipient2")
            .messageContent("messageContent")
            .build();
    SMSSendResponseDto expectedResponseDto1 =
        new SMSSendResponseDto("someSid1", MessageStatus.SENT);
    SMSSendResponseDto expectedResponseDto2 =
        new SMSSendResponseDto("someSid2", MessageStatus.SENT);
    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);
    when(twilioService.sendSMSFromService(anyString(), eq("messageContent")))
        .thenReturn(mockMessage1, mockMessage2);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class)))
        .thenReturn(savedDocument1, savedDocument2);

    when(dtoDocumentMapper.toSMSSendResponseDto(any(MessageDocument.class)))
        .thenReturn(expectedResponseDto1, expectedResponseDto2);

    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument1, savedDocument2);

    // when
    SMSBulkSendResponseDto actualResponseDto = smsService.sendBulkSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(2, actualResponseDto.messages().size());
    assertEquals(expectedResponseDto1, actualResponseDto.messages().getFirst());
    assertEquals(expectedResponseDto2, actualResponseDto.messages().get(1));
    verify(twilioService, times(2)).sendSMSFromService(anyString(), eq("messageContent"));
    verify(smsRepository, times(2)).save(any(MessageDocument.class));
  }

  @Test
  public void whenScheduleBulkSMS_thenSuccess() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSBulkScheduleRequestDto requestDto =
        new SMSBulkScheduleRequestDto(
            "sender", "messageContent", List.of("recipient1", "recipient2"), scheduledTime);
    MessageDocument savedDocument1 =
        MessageDocument.builder()
            .id("someId1")
            .recipient("recipient1")
            .messageContent("messageContent")
            .serviceSid("someSid1")
            .build();
    MessageDocument savedDocument2 =
        MessageDocument.builder()
            .id("someId2")
            .recipient("recipient2")
            .messageContent("messageContent")
            .serviceSid("someSid2")
            .build();
    SMSSendResponseDto expectedResponseDto1 =
        new SMSSendResponseDto("someSid1", MessageStatus.SCHEDULED);
    SMSSendResponseDto expectedResponseDto2 =
        new SMSSendResponseDto("someSid2", MessageStatus.SCHEDULED);
    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);
    when(twilioService.scheduleSMS(anyString(), anyString(), eq(scheduledTime)))
        .thenReturn(mockMessage1, mockMessage2);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class)))
        .thenReturn(savedDocument1, savedDocument2);

    when(dtoDocumentMapper.toSMSSendResponseDto(any(MessageDocument.class)))
        .thenReturn(expectedResponseDto1, expectedResponseDto2);

    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument1, savedDocument2);

    // when
    SMSBulkScheduleResponseDto actualResponseDto = smsService.scheduleBulkSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(2, actualResponseDto.messages().size());
    assertEquals(expectedResponseDto1, actualResponseDto.messages().getFirst());
    assertEquals(expectedResponseDto2, actualResponseDto.messages().get(1));
    verify(twilioService, times(2)).scheduleSMS(anyString(), anyString(), any(ZonedDateTime.class));
    verify(smsRepository, times(2)).save(any(MessageDocument.class));
  }

  @Test
  public void whenUpdateSMSStatusDelivered_thenSuccess() {
    // given
    String messageId = "someId";
    MessageDocument savedDocument =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto("accountSid", messageId, "sender", "delivered", null, null);
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument);
    // when
    smsService.updateSMSStatus(twilioStatusCallbackDto);

    verify(smsRepository, times(1)).save(savedDocument);
    // verify that the status was updated
    assertEquals(MessageStatus.DELIVERED, savedDocument.getStatus());
  }

  @Test
  public void whenUpdateSMSStatusFailed_thenSuccess() {
    // given
    String messageId = "someId";
    MessageDocument savedDocument =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto(
            "accountSid", messageId, "sender", "failed", "errorCode", "errorMessage");
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(MessageDocument.class))).thenReturn(savedDocument);
    // when
    smsService.updateSMSStatus(twilioStatusCallbackDto);

    verify(smsRepository, times(1)).save(savedDocument);
    // verify that the status was updated
    assertEquals(MessageStatus.FAILED, savedDocument.getStatus());
    assertEquals("errorCode", savedDocument.getErrorCode());
    assertEquals("errorMessage", savedDocument.getErrorMessage());
  }

  @Test
  public void whenUpdateSMSStatus_thenDomainException() {
    // given
    String messageId = "someId";
    MessageDocument savedDocument =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto(
            "accountSid", messageId, "sender", "failed", "errorCode", "errorMessage");
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(MessageDocument.class))).thenThrow(new DataAccessException("Failed to save SMS") {});

    DomainException thrownException =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.updateSMSStatus(twilioStatusCallbackDto);
            });

    assertNotNull(thrownException);
    assertEquals(Error.INVALID_REQUEST.getCode(), thrownException.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), thrownException.getStatus());
  }

    @Test
  public void whenUpdateSMSStatus_thenRuntimeException() {
    // given
    String messageId = "someId";

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto(
            "accountSid", messageId, "sender", "failed", "errorCode", "errorMessage");
    when(smsRepository.findById(messageId)).thenThrow(new RuntimeException("Can't connect to DB") {});

    DomainException thrownException =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.updateSMSStatus(twilioStatusCallbackDto);
            });

    assertNotNull(thrownException);
    assertEquals(Error.UNEXPECTED_ERROR.getCode(), thrownException.getCode());
    assertEquals(Error.UNEXPECTED_ERROR.getStatus(), thrownException.getStatus());
  }

  @Test
  public void whenSendSMS_thenApiExceptionThrown() {
    // given
    SMSSendRequestDto requestDto =
        new SMSSendRequestDto("recipient", "recipient", "messageContent");

    // Stub the twilioService.sendSMS call to throw an ApiException
    when(twilioService.sendSMSFromNumber(eq(requestDto.sender()), eq(requestDto.recipient()), eq("messageContent")))
        .thenThrow(new DomainException(Error.INVALID_REQUEST));

    // when
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.sendSMS(requestDto);
            });

    // then
    assertNotNull(exception);
    assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
  }

  @Test
  public void whenSendSMS_thenDomainAccessExceptionThrown() {
    // given
    SMSSendRequestDto requestDto = new SMSSendRequestDto("sender", "recipient", "messageContent");
    MessageDocument document =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();
    when(twilioService.sendSMSFromNumber(eq(requestDto.recipient()), eq(requestDto.sender()), eq("messageContent")))
        .thenReturn(mock(IMessageWrapper.class));

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(document);

    // Simulate DataAccessException
    when(smsRepository.save(document)).thenThrow(new DataAccessException("Failed to save SMS") {});

    // when
    DomainException thrownException =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.sendSMS(requestDto);
            });

    // then
    assertNotNull(thrownException);
    assertEquals(Error.INVALID_REQUEST.getCode(), thrownException.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), thrownException.getStatus());
  }

  @Test
  public void whenScheduleSMS_thenApiExceptionThrown() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSScheduleRequestDto requestDto =
        new SMSScheduleRequestDto("sender", "recipient", "messageContent", scheduledTime);

    when(twilioService.scheduleSMS(anyString(), eq("messageContent"), eq(scheduledTime)))
        .thenThrow(new DomainException(Error.INVALID_REQUEST));

    // when
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.scheduleSMS(requestDto);
            });

    // then
    assertNotNull(exception);
    assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
  }

  @Test
  public void whenScheduleSMS_thenDomainAccessExceptionThrown() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSScheduleRequestDto requestDto =
        new SMSScheduleRequestDto("sender", "recipient", "messageContent", scheduledTime);
    MessageDocument document =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    when(twilioService.scheduleSMS(anyString(), eq("messageContent"), eq(scheduledTime)))
        .thenReturn(mock(IMessageWrapper.class));

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(document);

    // Simulate DataAccessException
    when(smsRepository.save(document)).thenThrow(new DataAccessException("Failed to save SMS") {});

    // when
    DomainException thrownException =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.scheduleSMS(requestDto);
            });

    // then
    assertNotNull(thrownException);
    assertEquals(Error.INVALID_REQUEST.getCode(), thrownException.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), thrownException.getStatus());
  }

  @Test
  public void whenBulkSendSMS_thenApiExceptionThrown() {
    // given
    SMSBulkSendRequestDto requestDto =
        new SMSBulkSendRequestDto("sender", "messageContent", List.of("recipient1", "recipient2"));
    when(twilioService.sendSMSFromService(anyString(), eq("messageContent")))
        .thenThrow(new DomainException(Error.INVALID_REQUEST));
    // when
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.sendBulkSMS(requestDto);
            });

    // then
    assertNotNull(exception);
    assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
  }

  @Test
  public void whenBulkSendSMS_thenDomainAccessExceptionThrown() {
    // given
    SMSBulkSendRequestDto requestDto =
        new SMSBulkSendRequestDto("sender", "messageContent", List.of("recipient1", "recipient2"));
    MessageDocument document =
        MessageDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    when(twilioService.sendSMSFromService(anyString(), eq("messageContent")))
        .thenReturn(mock(IMessageWrapper.class));

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(document);

    // Simulate DataAccessException
    when(smsRepository.save(document)).thenThrow(new DataAccessException("Failed to save SMS") {});

    // when
    DomainException thrownException =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.sendBulkSMS(requestDto);
            });

    // then
    assertNotNull(thrownException);
    assertEquals(Error.INVALID_REQUEST.getCode(), thrownException.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), thrownException.getStatus());
  }

    @Test
  public void whenBulkScheduleSendSMS_thenApiExceptionThrown() {
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSBulkScheduleRequestDto requestDto =
        new SMSBulkScheduleRequestDto(
            "sender", "messageContent", List.of("recipient1", "recipient2"), scheduledTime);
    when(twilioService.scheduleSMS(eq("recipient1"), eq("messageContent"), eq(scheduledTime)))
        .thenThrow(new DomainException(Error.INVALID_REQUEST));
    MessageDocument document = MessageDocument.builder()
            .id("someId")
            .recipient("recipient1")
            .messageContent("messageContent")
            .build();
    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(document);
    // when
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> {
              smsService.scheduleBulkSMS(requestDto);
            });

    // then
    assertNotNull(exception);
    assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
    assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
  }
}
