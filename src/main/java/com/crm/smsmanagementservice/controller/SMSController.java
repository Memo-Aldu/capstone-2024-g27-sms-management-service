package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSSendResponseDto;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.service.sms.ISMSService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SMSSendResponseDto> sendSMS(@Valid @RequestBody SMSSendRequestDto smsSendRequest) {
        log.info("SMS send request received: {}", smsSendRequest);
        try {
            SMSSendResponseDto response = smsService.sendSMS(smsSendRequest);
            return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);
        } catch (Exception e) {
            throw new DomainException("000500", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send SMS");
        }
    }

    @PostMapping("/schedule")
    public ResponseEntity<SMSScheduleResponseDto> scheduleSMS(@Valid @RequestBody SMSScheduleRequestDto smsScheduleRequest) {
        log.info("SMS schedule request received: {}", smsScheduleRequest);
        try {
            SMSScheduleResponseDto response = smsService.scheduleSMS(smsScheduleRequest);
            return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);
        } catch (Exception e) {
            throw new DomainException("000500", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to schedule SMS");
        }
    }

    @PostMapping("/bulk/send")
    public ResponseEntity<SMSBulkSendResponseDto> sendBulkSMS(@Valid @RequestBody SMSBulkSendRequestDto bulkSmsRequest) {
        log.info("SMS  bulk send request received: {}", bulkSmsRequest);
        try {
            SMSBulkSendResponseDto response = smsService.sendBulkSMS(bulkSmsRequest);
            return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);
        } catch (Exception e) {
            throw new DomainException("000500", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send SMS");
        }
    }

    @PostMapping("/bulk/schedule")
    public ResponseEntity<SMSBulkScheduleResponseDto> scheduleBulkSMS(@Valid @RequestBody SMSBulkScheduleRequestDto bulkScheduleRequest) {
        log.info("SMS bulk schedule request received: {}", bulkScheduleRequest);
        try {
            SMSBulkScheduleResponseDto response = smsService.scheduleBulkSMS(bulkScheduleRequest);
            return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);
        } catch (Exception e) {
            throw new DomainException("000500", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to schedule SMS");
        }
    }
}