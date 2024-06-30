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
 * This class is a controller that handles HTTP requests related to the SMS service.
 * It has four endpoints: one for sending an SMS, one for scheduling an SMS, one for sending bulk SMS, and one for scheduling bulk SMS.
 * It uses the ISMSService to perform these operations.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@RestController
@RequestMapping("/api/v1/messages/sms")
@AllArgsConstructor
@Slf4j
public class SMSController {
    private final ISMSService smsService;

    /**
     * This method is an endpoint for sending an SMS.
     * It takes an SMSSendRequestDto object as a parameter, which contains the recipient's phone number and the content of the message.
     * It returns an SMSSendResponseDto object, which contains the ID and status of the sent message.
     */
    @PostMapping("/send")
    public ResponseEntity<SMSSendResponseDto> sendSMS(@Valid @RequestBody SMSSendRequestDto smsSendRequest) throws URISyntaxException {
        log.info("SMS send request received: {}", smsSendRequest);
        SMSSendResponseDto response = smsService.sendSMS(smsSendRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);

    }

    /**
     * This method is an endpoint for scheduling an SMS to be sent at a specific time.
     * It takes an SMSScheduleRequestDto object as a parameter, which contains the recipient's phone number, the content of the message, and the time at which the message should be sent.
     * It returns an SMSScheduleResponseDto object, which contains the ID and status of the scheduled message, and the time at which it is scheduled to be sent.
     */
    @PostMapping("/schedule")
    public ResponseEntity<SMSScheduleResponseDto> scheduleSMS(@Valid @RequestBody SMSScheduleRequestDto smsScheduleRequest) throws URISyntaxException {
        log.info("SMS schedule request received: {}", smsScheduleRequest);
        SMSScheduleResponseDto response = smsService.scheduleSMS(smsScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/" + response.messageId())).body(response);
    }

    /**
     * This method is an endpoint for sending an SMS to multiple recipients.
     * It takes an SMSBulkSendRequestDto object as a parameter, which contains a list of the recipients' phone numbers and the content of the message.
     * It returns an SMSBulkSendResponseDto object, which contains a list of SMSSendResponseDto objects, each of which contains the ID and status of a sent message.
     */
    @PostMapping("/bulk/send")
    public ResponseEntity<SMSBulkSendResponseDto> sendBulkSMS(@Valid @RequestBody SMSBulkSendRequestDto bulkSmsRequest) throws URISyntaxException {
        log.info("SMS  bulk send request received: {}", bulkSmsRequest);

        SMSBulkSendResponseDto response = smsService.sendBulkSMS(bulkSmsRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);

    }

    /**
     * This method is an endpoint for scheduling an SMS to be sent at a specific time to multiple recipients.
     * It takes an SMSBulkScheduleRequestDto object as a parameter, which contains a list of the recipients' phone numbers, the content of the message, and the time at which the message should be sent.
     * It returns an SMSBulkScheduleResponseDto object, which contains a list of SMSSendResponseDto objects, each of which contains the ID and status of a scheduled message, and the time at which it is scheduled to be sent.
     */
    @PostMapping("/bulk/schedule")
    public ResponseEntity<SMSBulkScheduleResponseDto> scheduleBulkSMS(@Valid @RequestBody SMSBulkScheduleRequestDto bulkScheduleRequest) throws URISyntaxException {
        log.info("SMS bulk schedule request received: {}", bulkScheduleRequest);
        SMSBulkScheduleResponseDto response = smsService.scheduleBulkSMS(bulkScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/sms/")).body(response);
    }
}