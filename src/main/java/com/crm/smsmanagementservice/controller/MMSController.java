package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.service.mms.IMMSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/26/2024, Tuesday
 */
@RestController
@RequestMapping("/api/v1/mms")
@RequiredArgsConstructor
@Slf4j(topic = "MMSController")
public class MMSController {
    private final IMMSService mmsService;

    @PostMapping("/send")
    public ResponseEntity<MMSSendResponseDto> sendMMS(@Valid @RequestBody MMSSendRequestDto mmsSendRequest) throws URISyntaxException {
        log.info("MMS send request received: {}", mmsSendRequest);
        MMSSendResponseDto response = mmsService.sendMMS(mmsSendRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/" + response.messageId())).body(response);
    }

    @PostMapping("/schedule")
    public ResponseEntity<MMSScheduleResponseDto> scheduleMMS(@Valid @RequestBody MMSScheduleRequestDto mmsScheduleRequest) throws URISyntaxException {
        log.info("MMS schedule request received: {}", mmsScheduleRequest);
        MMSScheduleResponseDto response = mmsService.scheduleMMS(mmsScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/" + response.messageId())).body(response);
    }

    @PostMapping("/bulk/send")
    public ResponseEntity<MMSBulkSendResponseDto> sendBulkMMS(@Valid @RequestBody MMSBulkSendRequestDto bulkMmsRequest) throws URISyntaxException {
        log.info("MMS bulk send request received: {}", bulkMmsRequest);
        MMSBulkSendResponseDto response = mmsService.sendBulkMMS(bulkMmsRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/")).body(response);
    }

    @PostMapping("/bulk/schedule")
    public ResponseEntity<MMSBulkScheduleResponseDto> scheduleBulkMMS(@Valid @RequestBody MMSBulkScheduleRequestDto bulkScheduleRequest) throws URISyntaxException {
        log.info("MMS bulk schedule request received: {}", bulkScheduleRequest);
        MMSBulkScheduleResponseDto response = mmsService.scheduleBulkMMS(bulkScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/")).body(response);
    }
}
