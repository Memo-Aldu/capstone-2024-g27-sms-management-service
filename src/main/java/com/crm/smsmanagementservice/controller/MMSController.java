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
 * This class is a controller that handles HTTP requests related to multimedia messaging service (MMS).
 * It has four endpoints: one for sending an MMS, one for scheduling an MMS, one for sending bulk MMS, and one for scheduling bulk MMS.
 * It uses the IMMSService to perform these operations.
 *
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

    /**
     * This method is an endpoint for sending an MMS.
     * It takes an MMSSendRequestDto object as a parameter, which contains the recipient's phone number, the content of the message, and the URLs of the media files to be included in the message.
     * It returns an MMSSendResponseDto object, which contains the ID and status of the sent message.
     */
    @PostMapping("/send")
    public ResponseEntity<MMSSendResponseDto> sendMMS(@Valid @RequestBody MMSSendRequestDto mmsSendRequest) throws URISyntaxException {
        log.info("MMS send request received: {}", mmsSendRequest);
        MMSSendResponseDto response = mmsService.sendMMS(mmsSendRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/" + response.messageId())).body(response);
    }

    /**
     * This method is an endpoint for scheduling an MMS to be sent at a specific time.
     * It takes an MMSScheduleRequestDto object as a parameter, which contains the recipient's phone number, the content of the message, the URLs of the media files to be included in the message, and the time at which the message should be sent.
     * It returns an MMSScheduleResponseDto object, which contains the ID and status of the scheduled message, and the time at which it is scheduled to be sent.
     */
    @PostMapping("/schedule")
    public ResponseEntity<MMSScheduleResponseDto> scheduleMMS(@Valid @RequestBody MMSScheduleRequestDto mmsScheduleRequest) throws URISyntaxException {
        log.info("MMS schedule request received: {}", mmsScheduleRequest);
        MMSScheduleResponseDto response = mmsService.scheduleMMS(mmsScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/" + response.messageId())).body(response);
    }

    /**
     * This method is an endpoint for sending an MMS to multiple recipients.
     * It takes an MMSBulkSendRequestDto object as a parameter, which contains a list of the recipients' phone numbers, the content of the message, and the URLs of the media files to be included in the message.
     * It returns an MMSBulkSendResponseDto object, which contains a list of MMSSendResponseDto objects, each of which contains the ID and status of a sent message.
     */
    @PostMapping("/bulk/send")
    public ResponseEntity<MMSBulkSendResponseDto> sendBulkMMS(@Valid @RequestBody MMSBulkSendRequestDto bulkMmsRequest) throws URISyntaxException {
        log.info("MMS bulk send request received: {}", bulkMmsRequest);
        MMSBulkSendResponseDto response = mmsService.sendBulkMMS(bulkMmsRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/")).body(response);
    }

    /**
     * This method is an endpoint for scheduling an MMS to be sent at a specific time to multiple recipients.
     * It takes an MMSBulkScheduleRequestDto object as a parameter, which contains a list of the recipients' phone numbers, the content of the message, the URLs of the media files to be included in the message, and the time at which the message should be sent.
     * It returns an MMSBulkScheduleResponseDto object, which contains a list of MMSSendResponseDto objects, each of which contains the ID and status of a scheduled message, and the time at which it is scheduled to be sent.
     */
    @PostMapping("/bulk/schedule")
    public ResponseEntity<MMSBulkScheduleResponseDto> scheduleBulkMMS(@Valid @RequestBody MMSBulkScheduleRequestDto bulkScheduleRequest) throws URISyntaxException {
        log.info("MMS bulk schedule request received: {}", bulkScheduleRequest);
        MMSBulkScheduleResponseDto response = mmsService.scheduleBulkMMS(bulkScheduleRequest);
        return ResponseEntity.created(new URI("/api/v1/mms/")).body(response);
    }
}
