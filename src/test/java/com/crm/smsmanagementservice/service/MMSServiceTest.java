package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.mapper.MessageDocumentMapper;
import com.crm.smsmanagementservice.repository.SMSRepository;
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
  @Mock private SMSRepository smsRepository;
  @Mock private MessageDocumentMapper messageDocumentMapper;
  @Mock private IMessagingService twilioService;
  @InjectMocks private MMSService mmsService;

  @Test
  public void whenSendMMS_thenSuccess() {
    MMSSendRequestDto request =
        new MMSSendRequestDto(
                "1234567890", "1234567890",
                "Hello, World!", List.of("http://example.com/image.jpg"));
    SmSDocument document =
        SmSDocument.builder()
            .id("MESSAGE_ID").recipient("1234567890").sender("1234567890")
            .messageContent("Hello, World!").mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.sendMMSFromNumber(
            request.recipient(), request.messageContent(), request.mediaUrls()))
        .thenReturn(mockMessage);
    when(smsRepository.save(any())).thenReturn(document);
    MMSSendResponseDto responseDto = mmsService.sendMMS(request);
    assertEquals("MESSAGE_ID", responseDto.messageId());
  }

  @Test
  public void whenScheduleMMS_thenSuccess() {
    ZonedDateTime now = ZonedDateTime.now();
    MMSScheduleRequestDto request =
        new MMSScheduleRequestDto(
            "1234567890", "1234567890",
            "Hello, World!", now,
            List.of("http://example.com/image.jpg"));
    SmSDocument document =
        SmSDocument.builder()
            .id("MESSAGE_ID").recipient("1234567890")
            .sender("1234567890").messageContent("Hello, World!")
            .scheduledTime(now).mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();

    IMessageWrapper mockMessage = mock(IMessageWrapper.class);
    when(twilioService.scheduleMMS(
            request.recipient(), request.messageContent(),
            request.mediaUrls(), request.scheduleTime()))
        .thenReturn(mockMessage);
    when(smsRepository.save(any())).thenReturn(document);
    MMSScheduleResponseDto responseDto = mmsService.scheduleMMS(request);
    assertEquals("MESSAGE_ID", responseDto.messageId());
    assertEquals(now, responseDto.scheduleTime());
  }

  @Test
  public void whenSendBulkMMS_thenSuccess() {
    MMSBulkSendRequestDto request =
        new MMSBulkSendRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567890", "1234567891"), List.of("http://example.com/image.jpg"));
    SmSDocument document1 =
        SmSDocument.builder()
            .id("MESSAGE_ID1").recipient("1234567890")
            .sender("1234567890").messageContent("Hello, World!")
            .mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    SmSDocument document2 =
        SmSDocument.builder()
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
    when(smsRepository.save(any())).thenReturn(document1).thenReturn(document2);
    MMSBulkSendResponseDto response = mmsService.sendBulkMMS(request);
    assertEquals(2, response.messages().size());
    assertEquals("MESSAGE_ID2", response.messages().get(0).messageId());
    assertEquals("MESSAGE_ID1", response.messages().get(1).messageId());
  }

  @Test
  public void whenScheduleBulkMMS_thenSuccess() {
    ZonedDateTime now = ZonedDateTime.now();
    MMSBulkScheduleRequestDto request =
        new MMSBulkScheduleRequestDto(
            "1234567890", "Hello, World!",
            List.of("1234567890", "1234567891"), now, List.of("http://example.com/image.jpg"));
    SmSDocument document1 =
        SmSDocument.builder()
                .id("MESSAGE_ID1").recipient("1234567890")
                .sender("1234567890").messageContent("Hello, World!")
                .scheduledTime(now).mediaUrls(Map.of("1", "http://example.com/image.jpg")).build();
    SmSDocument document2 =
        SmSDocument.builder()
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
    when(smsRepository.save(any())).thenReturn(document1).thenReturn(document2);
    MMSBulkScheduleResponseDto response = mmsService.scheduleBulkMMS(request);
    assertEquals(2, response.messages().size());
    assertEquals("MESSAGE_ID1", response.messages().get(0).messageId());
    assertEquals("MESSAGE_ID2", response.messages().get(1).messageId());
    assertEquals(now, response.scheduleTime());
  }
}
