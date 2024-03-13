package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.TwilioStatusCallbackDto;
import com.crm.smsmanagementservice.service.sms.ISMSService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/26/2024, Monday
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/callback")
@AllArgsConstructor
public class CallbackController {
    private final ISMSService smsService;

    @PostMapping(value = "/sms-status")
    public void smsStatusCallback(@RequestParam(value = "MessageStatus", required = false) String messageStatus,
                       @RequestParam(value = "ApiVersion", required = false) String apiVersion,
                       @RequestParam(value = "SmsSid", required = false) String smsSid,
                       @RequestParam(value = "SmsStatus", required = false) String smsStatus,
                       @RequestParam(value = "To", required = false) String to,
                       @RequestParam(value = "From", required = false) String from,
                       @RequestParam(value = "MessageSid", required = false) String messageSid,
                       @RequestParam(value = "AccountSid", required = false) String accountSid) {
        log.info("SMS status callback received: {}", messageStatus);
        smsService.updateSMSStatus(TwilioStatusCallbackDto.builder()
                .accountSid(accountSid)
                .messageSid(messageSid)
                .smsSid(smsSid)
                .smsStatus(smsStatus)
                .build());
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> healthCheck() {
        log.info("Health check endpoint accessed");
        return ResponseEntity.ok().build();
    }
}