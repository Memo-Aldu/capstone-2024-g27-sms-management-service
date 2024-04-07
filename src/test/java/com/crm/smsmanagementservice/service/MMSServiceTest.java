package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.mapper.DocumentMapper;
import com.crm.smsmanagementservice.mapper.DtoMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.service.message.IMessagingService;
import com.crm.smsmanagementservice.service.mms.MMSService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MMSServiceTest {
  @Mock private MessageRepository smsRepository;
  @Mock private DocumentMapper messageDocumentMapper;
  @Mock private DtoMapper dtoMapper;
  @Mock private IMessagingService twilioService;
  @InjectMocks private MMSService mmsService;

  @Test
  public void whenSendMMS_thenSuccess() {
    MMSSendRequestDto request =
        new MMSSendRequestDto(
                "1234567890", "1234567890",
                "Hello, World!", List.of("http://example.com/image.jpg"));
    MessageDocument document =
        MessageDocument.builder()
            .id("MESSAGE_ID").recipient("1234567890").sender("1234567890")
            .messageContent("Hello, World!").mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.sendMMSFromNumber(
            request.recipient(), request.sender(), request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage);
    when(smsRepository.save(any())).thenReturn(document);
    when(messageDocumentMapper.toDocument(mockMessage)).thenReturn(document);
    when(dtoMapper.toMMSSendResponseDto(document)).thenReturn(new MMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED));
    MMSSendResponseDto responseDto = mmsService.sendMMS(request);
    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals(MessageStatus.DELIVERED, responseDto.status());
  }

  @Test
  public void whenScheduleMMS_thenSuccess() {
    ZonedDateTime now = ZonedDateTime.now();
    MMSScheduleRequestDto request =
        new MMSScheduleRequestDto(
            "1234567890", "1234567890",
            "Hello, World!", now,
            List.of("http://example.com/image.jpg"));
    MessageDocument document =
        MessageDocument.builder()
            .id("MESSAGE_ID").recipient("1234567890")
            .sender("1234567890").messageContent("Hello, World!")
            .scheduledTime(now).mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();

    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.scheduleMMS(
            request.recipient(), request.messageContent(),
            request.mediaUrls(), request.scheduleTime()))
        .thenReturn(mockMessage);
    when(smsRepository.save(any())).thenReturn(document);
    when(messageDocumentMapper.toDocument(mockMessage)).thenReturn(document);
    when(dtoMapper.toMMSScheduleResponseDto(document)).thenReturn(new MMSScheduleResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, now));
    MMSScheduleResponseDto responseDto = mmsService.scheduleMMS(request);
    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals(MessageStatus.SCHEDULED, responseDto.status());
    assertEquals(now, responseDto.scheduleTime());
  }

  @Test
  public void whenSendBulkMMS_thenSuccess() {
    MMSBulkSendRequestDto request =
        new MMSBulkSendRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567890", "1234567891"), List.of("http://example.com/image.jpg"));
    MessageDocument document1 =
        MessageDocument.builder()
            .id("MESSAGE_ID1").recipient("1234567890")
            .sender("1234567890").messageContent("Hello, World!")
            .mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    MessageDocument document2 =
        MessageDocument.builder()
            .id("MESSAGE_ID2").recipient("1234567891")
            .sender("1234567890").messageContent("Hello, World!")
            .mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();

    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);
    when(twilioService.sendMMSFromService(
            "1234567890", request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage1);
    when(twilioService.sendMMSFromService(
            "1234567891", request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage2);
    when(messageDocumentMapper.toDocument(mockMessage1)).thenReturn(document1);
    when(messageDocumentMapper.toDocument(mockMessage2)).thenReturn(document2);
    when(smsRepository.save(any(MessageDocument.class))).thenReturn(document1, document2);
    when(dtoMapper.toMMSSendResponseDto(document1)).thenReturn(new MMSSendResponseDto("MESSAGE_ID1", MessageStatus.DELIVERED));
    when(dtoMapper.toMMSSendResponseDto(document2)).thenReturn(new MMSSendResponseDto("MESSAGE_ID2", MessageStatus.DELIVERED));
    MMSBulkSendResponseDto response = mmsService.sendBulkMMS(request);
    assertEquals(2, response.messages().size());
  }

  @Test
  public void whenScheduleBulkMMS_thenSuccess() {
    ZonedDateTime now = ZonedDateTime.now();
    MMSBulkScheduleRequestDto request =
        new MMSBulkScheduleRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567890", "1234567891"), now, List.of("http://example.com/image.jpg"));
    MessageDocument document1 =
        MessageDocument.builder()
                .id("MESSAGE_ID1").recipient("1234567890")
                .sender("1234567890").messageContent("Hello, World!")
                .scheduledTime(now).mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    MessageDocument document2 =
        MessageDocument.builder()
                .id("MESSAGE_ID2").recipient("1234567891")
                .sender("1234567890").messageContent("Hello, World!")
                .scheduledTime(now).mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();

    IMessageWrapper mockMessage1 = mock(IMessageWrapper.class);
    IMessageWrapper mockMessage2 = mock(IMessageWrapper.class);
    when(twilioService.scheduleMMS(
            "1234567890", request.messageContent(), request.mediaUrls(), now))
        .thenReturn(mockMessage1);
    when(twilioService.scheduleMMS(
            "1234567891", request.messageContent(), request.mediaUrls(), now))
        .thenReturn(mockMessage2);
    when(messageDocumentMapper.toDocument(mockMessage1)).thenReturn(document1);
    when(messageDocumentMapper.toDocument(mockMessage2)).thenReturn(document2);
    when(smsRepository.save(any(MessageDocument.class))).thenReturn(document1, document2);
    when(dtoMapper.toMMSSendResponseDto(document1)).thenReturn(new MMSSendResponseDto("MESSAGE_ID1", MessageStatus.SCHEDULED));
    when(dtoMapper.toMMSSendResponseDto(document2)).thenReturn(new MMSSendResponseDto("MESSAGE_ID2", MessageStatus.SCHEDULED));
    MMSBulkScheduleResponseDto response = mmsService.scheduleBulkMMS(request);
    assertEquals(2, response.messages().size());
    assertEquals(now, response.scheduleTime());
  }
}
