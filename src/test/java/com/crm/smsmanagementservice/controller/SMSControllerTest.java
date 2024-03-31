package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.sms.ISMSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SMSControllerTest {

  @InjectMocks private SMSController smsController;

  @Mock private ISMSService smsService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldSendSMSSuccessfully() throws URISyntaxException {
    SMSSendRequestDto request = new SMSSendRequestDto("1234567890", "1234567890", "Hello, World!");
    SMSSendResponseDto response = new SMSSendResponseDto("MESSAGE_ID", MessageStatus.DELIVERED);
    when(smsService.sendSMS(request)).thenReturn(response);

    ResponseEntity<SMSSendResponseDto> result = smsController.sendSMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldScheduleSMSSuccessfully() throws URISyntaxException {
    SMSScheduleRequestDto request =
        new SMSScheduleRequestDto("1234567890", "1234567890", "Hello, World!", ZonedDateTime.now());
    SMSScheduleResponseDto response =
        new SMSScheduleResponseDto("MESSAGE_ID", MessageStatus.SCHEDULED, ZonedDateTime.now());
    when(smsService.scheduleSMS(request)).thenReturn(response);

    ResponseEntity<SMSScheduleResponseDto> result = smsController.scheduleSMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldSendBulkSMSSuccessfully() throws URISyntaxException {
    SMSBulkSendRequestDto request =
        new SMSBulkSendRequestDto(
            "1234567890", "Hello, World!", List.of("1234567890", "1234567890"));
    SMSBulkSendResponseDto response =
        new SMSBulkSendResponseDto(
            List.of(
                new SMSSendResponseDto("MESSAGE_ID1", MessageStatus.DELIVERED),
                new SMSSendResponseDto("MESSAGE_ID2", MessageStatus.DELIVERED)));
    when(smsService.sendBulkSMS(request)).thenReturn(response);

    ResponseEntity<SMSBulkSendResponseDto> result = smsController.sendBulkSMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }

  @Test
  public void shouldScheduleBulkSMSSuccessfully() throws URISyntaxException {
    SMSBulkScheduleRequestDto request =
        new SMSBulkScheduleRequestDto(
            "1234567890",
            "Hello, World!",
            List.of("1234567890", "1234567890"),
            ZonedDateTime.now());
    SMSBulkScheduleResponseDto response =
        new SMSBulkScheduleResponseDto(
            List.of(
                new SMSSendResponseDto("MESSAGE_ID1", MessageStatus.DELIVERED),
                new SMSSendResponseDto("MESSAGE_ID2", MessageStatus.DELIVERED)),
            ZonedDateTime.now());
    when(smsService.scheduleBulkSMS(request)).thenReturn(response);

    ResponseEntity<SMSBulkScheduleResponseDto> result = smsController.scheduleBulkSMS(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(response, result.getBody());
  }
}
