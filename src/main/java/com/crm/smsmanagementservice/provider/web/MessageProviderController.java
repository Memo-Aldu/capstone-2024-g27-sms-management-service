package com.crm.smsmanagementservice.provider.web;

import com.crm.smsmanagementservice.provider.MessagingProviderExternalAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-11, Thursday
 */

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/provider") @Slf4j(topic = "MESSAGE_PROVIDER_CONTROLLER")
public class MessageProviderController {
    private final MessagingProviderExternalAPI messagingProvider;

    @PostMapping("/callback/message-status")
    public ResponseEntity<?> handleCallback(
            @RequestParam(value = "MessageStatus", required = false) String messageStatus,
            @RequestParam(value = "MessageSid", required = true) String messageSid,
            @RequestParam(value = "AccountSid", required = true) String accountSid,
            @RequestParam(value = "MessagingServiceSid", required = false) String serviceSid,
            @RequestParam(value = "ErrorCode", required = false) String errorCode,
            @RequestParam(value = "ErrorMessage", required = false) String errorMessage
    ) {
        log.info("Received message status update: {}", messageSid);
        MessageStatusUpdateDTO messageStatusUpdate = MessageStatusUpdateDTO.builder()
                .messageStatus(messageStatus).messageId(messageSid)
                .accountId(accountSid).serviceId(serviceSid)
                .errorCode(errorCode).errorMessage(errorMessage)
                .build();

        messagingProvider.handleIncomingMessageStatusUpdate(messageStatusUpdate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback/inbound-message")
    public ResponseEntity<?> handleInboundMessage(
            @RequestParam(value = "MessageStatus", required = false) String messageStatus,
            @RequestParam(value = "ApiVersion", required = false) String apiVersion,
            @RequestParam(value = "To", required = false) String to,
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "Body", required = false) String body,
            @RequestParam(value = "MessageSid", required = true) String messageSid,
            @RequestParam(value = "AccountSid", required = true) String accountSid,
            @RequestParam(value = "MessagingServiceSid", required = false) String serviceSid,
            @RequestParam(value = "ErrorCode", required = false) String errorCode,
            @RequestParam(value = "NumSegments", required = false) String segments,
            @RequestParam(value = "NumMedia", required = false) String mediaLength,
            @RequestParam(value = "MediaContentType", required = false) Set<String> sortedMediaTypes,
            @RequestParam(value = "MediaUrl", required = false) Set<String> sortedMediaUrls,
            @RequestParam(value = "ErrorMessage", required = false) String errorMessage
    ) {
        log.info("Received inbound message: {}", messageSid);
        InboundMessageDTO inboundMessage = InboundMessageDTO.builder()
                .messageStatus(messageStatus).apiVersion(apiVersion)
                .to(to).from(from).body(body).messageId(messageSid)
                .accountId(accountSid).serviceId(serviceSid)
                .errorCode(errorCode).segments(segments)
                .mediaLength(mediaLength).sortedMediaTypes(sortedMediaTypes)
                .sortedMediaUrls(sortedMediaUrls).errorMessage(errorMessage)
                .build();
        messagingProvider.handleIncomingMessage(inboundMessage);
        return ResponseEntity.ok().build();
    }
}
