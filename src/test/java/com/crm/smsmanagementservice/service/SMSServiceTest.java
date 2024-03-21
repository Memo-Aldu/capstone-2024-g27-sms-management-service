package com.crm.smsmanagementservice.service;


import com.crm.smsmanagementservice.dto.request.*;
import com.crm.smsmanagementservice.dto.response.*;
import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.DtoDocumentMapper;
import com.crm.smsmanagementservice.mapper.MessageDocumentMapper;
import com.crm.smsmanagementservice.repository.SMSRepository;
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

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/14/2024, Thursday
 */
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class SMSServiceTest {
  @Mock private SMSRepository smsRepository;
  @Mock private MessageDocumentMapper messageDocumentMapper;
  @Mock private DtoDocumentMapper dtoDocumentMapper;
  @Mock private IMessagingService twilioService;
  @InjectMocks private SMSService smsService;
  @Captor private ArgumentCaptor<SmSDocument> documentCaptor;


  @Test
  public void whenPollSMSStatus_thenDocumentStatusUpdated() {
    // given
    SmSDocument document =
        SmSDocument.builder()
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
    List<SmSDocument> nonTerminalMessages = List.of(document);
    when(smsRepository.findAllByStatus(NON_TERMINAL_STATUSES)).thenReturn(nonTerminalMessages);

    IMessageWrapper message = mock(IMessageWrapper.class);
    when(twilioService.pollMessageStatus()).thenReturn(true);
    when(twilioService.fetchMessageById("someId")).thenReturn(message);
    when(message.getStatus()).thenReturn(MessageStatus.DELIVERED);

    // when
    smsService.pollSMSStatus();

    // then
    verify(smsRepository).save(documentCaptor.capture());
    SmSDocument capturedDocument = documentCaptor.getValue();
    assertEquals(MessageStatus.DELIVERED, capturedDocument.getStatus());
  }

    @Test
  public void whenPollSMSStatusFailed_thenDocumentStatusUpdated() {
    // given
    SmSDocument document =
        SmSDocument.builder()
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
    List<SmSDocument> nonTerminalMessages = List.of(document);
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
    SmSDocument capturedDocument = documentCaptor.getValue();
    assertEquals(MessageStatus.FAILED, capturedDocument.getStatus());
    assertEquals("errorCode", capturedDocument.getErrorCode());
    assertEquals("errorMessage", capturedDocument.getErrorMessage());
  }

  @Test
  public void whenSendSMS_thenSuccess() {
    // given
    SMSSendRequestDto requestDto = new SMSSendRequestDto("sender", "recipient", "messageContent");
    SmSDocument savedDocument =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();
    SMSSendResponseDto expectedResponseDto = new SMSSendResponseDto("someSid", MessageStatus.SENT);

    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.sendSMSFromNumber(requestDto.recipient(), requestDto.messageContent()))
        .thenReturn(mockMessage);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(savedDocument);

    when(dtoDocumentMapper.toSMSSendResponseDto(any(SmSDocument.class)))
        .thenReturn(expectedResponseDto);

    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument);

    // when
    SMSSendResponseDto actualResponseDto = smsService.sendSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(expectedResponseDto, actualResponseDto);
    verify(twilioService, times(1))
        .sendSMSFromNumber(requestDto.recipient(), requestDto.messageContent());
    verify(smsRepository, times(1)).save(savedDocument);
  }

  @Test
  public void whenScheduledSMS_thenSuccess() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSScheduleRequestDto requestDto =
        new SMSScheduleRequestDto("sender", "recipient", "messageContent", scheduledTime);
    SmSDocument savedDocument =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .serviceSid("someSid")
            .build();
    SMSScheduleResponseDto expectedResponseDto =
        new SMSScheduleResponseDto("someSid", MessageStatus.SCHEDULED, scheduledTime);
    IMessageWrapper mockMessage = mock(IMessageWrapper.class);

    when(messageDocumentMapper.toDocument(any(IMessageWrapper.class))).thenReturn(savedDocument);

    when(dtoDocumentMapper.toSMSScheduleResponseDto(any(SmSDocument.class)))
        .thenReturn(expectedResponseDto);

    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument);
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
    SmSDocument savedDocument1 =
        SmSDocument.builder()
            .id("someId1")
            .recipient("recipient1")
            .messageContent("messageContent")
            .build();
    SmSDocument savedDocument2 =
        SmSDocument.builder()
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

    when(dtoDocumentMapper.toSMSSendResponseDto(any(SmSDocument.class)))
        .thenReturn(expectedResponseDto1, expectedResponseDto2);

    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument1, savedDocument2);

    // when
    SMSBulkSendResponseDto actualResponseDto = smsService.sendBulkSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(2, actualResponseDto.messages().size());
    assertEquals(expectedResponseDto1, actualResponseDto.messages().getFirst());
    assertEquals(expectedResponseDto2, actualResponseDto.messages().get(1));
    verify(twilioService, times(2)).sendSMSFromService(anyString(), eq("messageContent"));
    verify(smsRepository, times(2)).save(any(SmSDocument.class));
  }

  @Test
  public void whenScheduleBulkSMS_thenSuccess() {
    // given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    SMSBulkScheduleRequestDto requestDto =
        new SMSBulkScheduleRequestDto(
            "sender", "messageContent", List.of("recipient1", "recipient2"), scheduledTime);
    SmSDocument savedDocument1 =
        SmSDocument.builder()
            .id("someId1")
            .recipient("recipient1")
            .messageContent("messageContent")
            .serviceSid("someSid1")
            .build();
    SmSDocument savedDocument2 =
        SmSDocument.builder()
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

    when(dtoDocumentMapper.toSMSSendResponseDto(any(SmSDocument.class)))
        .thenReturn(expectedResponseDto1, expectedResponseDto2);

    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument1, savedDocument2);

    // when
    SMSBulkScheduleResponseDto actualResponseDto = smsService.scheduleBulkSMS(requestDto);

    // then
    assertNotNull(actualResponseDto);
    assertEquals(2, actualResponseDto.messages().size());
    assertEquals(expectedResponseDto1, actualResponseDto.messages().getFirst());
    assertEquals(expectedResponseDto2, actualResponseDto.messages().get(1));
    verify(twilioService, times(2)).scheduleSMS(anyString(), anyString(), any(ZonedDateTime.class));
    verify(smsRepository, times(2)).save(any(SmSDocument.class));
  }

  @Test
  public void whenUpdateSMSStatusDelivered_thenSuccess() {
    // given
    String messageId = "someId";
    SmSDocument savedDocument =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto("accountSid", messageId, "sender", "delivered", null, null);
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument);
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
    SmSDocument savedDocument =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto(
            "accountSid", messageId, "sender", "failed", "errorCode", "errorMessage");
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(SmSDocument.class))).thenReturn(savedDocument);
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
    SmSDocument savedDocument =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();

    TwilioStatusCallbackDto twilioStatusCallbackDto =
        new TwilioStatusCallbackDto(
            "accountSid", messageId, "sender", "failed", "errorCode", "errorMessage");
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(savedDocument));
    when(smsRepository.save(any(SmSDocument.class))).thenThrow(new DataAccessException("Failed to save SMS") {});

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
    when(twilioService.sendSMSFromNumber(anyString(), eq("messageContent")))
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
    SmSDocument document =
        SmSDocument.builder()
            .id("someId")
            .recipient("recipient")
            .messageContent("messageContent")
            .build();
    when(twilioService.sendSMSFromNumber(anyString(), eq("messageContent")))
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
    SmSDocument document =
        SmSDocument.builder()
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
    SmSDocument document =
        SmSDocument.builder()
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
    SmSDocument document = SmSDocument.builder()
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
