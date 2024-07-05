package com.crm.smsmanagementservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.mms.IMMSService;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MMSControllerTest {

  @InjectMocks private MMSController mmsController;

  @Mock private IMMSService mmsService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldSendMMSSuccessfully() throws URISyntaxException {
    MMSSendRequestDto request = new MMSSendRequestDto(
            "1234567890", "1234567890",
            "Hello, World!", List.of("http://example.com/image.jpg"), "conversationId");
    MMSSendResponseDto response = new MMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED, "conversationId");
    when(mmsService.sendMMS(request)).thenReturn(response);

    ResponseEntity<MMSSendResponseDto> result = mmsController.sendMMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldScheduleMMSSuccessfully() throws URISyntaxException {
    ZonedDateTime now = ZonedDateTime.now();
    MMSScheduleRequestDto request =
        new MMSScheduleRequestDto("1234567890", "1234567890",
                "Hello, World!", now, List.of("http://example.com/image.jpg"), "conversationId");
    MMSScheduleResponseDto response =
        new MMSScheduleResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, now, "conversationId");
    when(mmsService.scheduleMMS(request)).thenReturn(response);

    ResponseEntity<MMSScheduleResponseDto> result = mmsController.scheduleMMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldSendBulkMMSSuccessfully() throws URISyntaxException {
    MMSBulkSendRequestDto request =
        new MMSBulkSendRequestDto(
            "1234567890", "Hello, World!", List.of("1234567890", "1234567890"),
                List.of("http://example.com/image.jpg"));
    MMSBulkSendResponseDto response =
        new MMSBulkSendResponseDto(
            List.of(
                new MMSSendResponseDto("MESSAGE_ID1", MessageStatus.DELIVERED, "conversationId"),
                new MMSSendResponseDto("MESSAGE_ID2", MessageStatus.DELIVERED, "conversationId")));
    when(mmsService.sendBulkMMS(request)).thenReturn(response);

    ResponseEntity<MMSBulkSendResponseDto> result = mmsController.sendBulkMMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldScheduleBulkMMSSuccessfully() throws URISyntaxException {
    ZonedDateTime now = ZonedDateTime.now();
    MMSBulkScheduleRequestDto request =
        new MMSBulkScheduleRequestDto(
            "1234567890",
            "Hello, World!",
            List.of("1234567890", "1234567890"),
            now, List.of("http://example.com/image.jpg"));
    MMSBulkScheduleResponseDto response =
        new MMSBulkScheduleResponseDto(
            List.of(
                new MMSSendResponseDto("MESSAGE_ID1", MessageStatus.DELIVERED, "conversationId"),
                new MMSSendResponseDto("MESSAGE_ID2", MessageStatus.DELIVERED, "conversationId")),
            now);
    when(mmsService.scheduleBulkMMS(request)).thenReturn(response);

    ResponseEntity<MMSBulkScheduleResponseDto> result = mmsController.scheduleBulkMMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }
}
