package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.service.message.IMessageStatusListenerService;
import com.crm.smsmanagementservice.twilio.dto.TwilioStatusCallbackDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class is a controller that handles HTTP requests related to callbacks from the Twilio API.
 * It has two endpoints: one for receiving status updates for SMS messages, and one for checking the health of the service.
 * It uses the ISMSService to update the status of SMS messages.
 *  * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/26/2024, Monday
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/callback")
@AllArgsConstructor
public class CallbackController {
    private final IMessageStatusListenerService messageStatusListenerService;

    /**
     * This method is a callback endpoint for receiving status updates for SMS messages from the Twilio API.
     * It takes several parameters, including the status of the message, the version of the API, the SID of the SMS, and any error information.
     * It logs the received information and uses the ISMSService to update the status of the SMS message.
     */
    @PostMapping(value = "/sms-status")
    public void smsStatusCallback(@RequestParam(value = "MessageStatus", required = false) String messageStatus,
                       @RequestParam(value = "ApiVersion", required = false) String apiVersion,
                       @RequestParam(value = "SmsSid", required = false) String smsSid,
                       @RequestParam(value = "SmsStatus", required = false) String smsStatus,
                       @RequestParam(value = "To", required = false) String to,
                       @RequestParam(value = "From", required = false) String from,
                       @RequestParam(value = "MessageSid", required = false) String messageSid,
                       @RequestParam(value = "AccountSid", required = false) String accountSid,
                       @RequestParam(value = "ErrorCode", required = false) String errorCode,
                       @RequestParam(value = "ErrorMessage", required = false) String errorMessage) {
        log.info("SMS status callback received: {} " +
                "with errorCode: {} and error: {}", messageStatus, errorCode, errorMessage);
        errorMessage = errorMessage == null ? "" : errorMessage;
        errorCode = errorCode == null ? "" : errorCode;
        messageStatusListenerService.onMessageStatusChanged(TwilioStatusCallbackDto.builder()
                .accountSid(accountSid).messageSid(messageSid)
                .smsSid(smsSid).smsStatus(smsStatus)
                .errorCode(errorCode).errorMessage(errorMessage).build());
    }

    /**
     * This method is a callback endpoint for receiving status updates for SMS messages from the Twilio API.
     * It takes several parameters, including the status of the message, the version of the API, the SID of the SMS, and any error information.
     * It logs the received information and uses the ISMSService to update the status of the SMS message.
     */
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> healthCheck() {
        log.info("Health check endpoint accessed");
        return ResponseEntity.ok().build();
    }
}