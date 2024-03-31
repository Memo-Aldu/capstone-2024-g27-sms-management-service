package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.service.sms.ISMSService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@RestController
@RequestMapping("/api/v1/sms")
@AllArgsConstructor
@Slf4j
public class SMSController {
    private final ISMSService smsService;

    @PostMapping("/send")
    public ResponseEntity<SMSSendResponseDto> sendSMS(@Valid @RequestBody SMSSendRequestDto smsSendRequest) throws URISyntaxException {
        log.info("SMS send request received: {}", smsSendRequest);
        SMSSendResponseDto response = smsService.sendSMS(smsSendRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);

    }

    @PostMapping("/schedule")
    public ResponseEntity<SMSScheduleResponseDto> scheduleSMS(@Valid @RequestBody SMSScheduleRequestDto smsScheduleRequest) throws URISyntaxException {
        log.info("SMS schedule request received: {}", smsScheduleRequest);
        SMSScheduleResponseDto response = smsService.scheduleSMS(smsScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);
    }

    @PostMapping("/bulk/send")
    public ResponseEntity<SMSBulkSendResponseDto> sendBulkSMS(@Valid @RequestBody SMSBulkSendRequestDto bulkSmsRequest) throws URISyntaxException {
        log.info("SMS  bulk send request received: {}", bulkSmsRequest);

        SMSBulkSendResponseDto response = smsService.sendBulkSMS(bulkSmsRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);

    }

    @PostMapping("/bulk/schedule")
    public ResponseEntity<SMSBulkScheduleResponseDto> scheduleBulkSMS(@Valid @RequestBody SMSBulkScheduleRequestDto bulkScheduleRequest) throws URISyntaxException {
        log.info("SMS bulk schedule request received: {}", bulkScheduleRequest);
        SMSBulkScheduleResponseDto response = smsService.scheduleBulkSMS(bulkScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);
    }
}