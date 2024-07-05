package com.crm.smsmanagementservice.service.message;

import com.crm.smsmanagementservice.dto.request.IMessageStatusCallback;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/1/2024, Saturday
 */
@RequiredArgsConstructor
@Service @Slf4j(topic = "MessageStatusListenerService")
public class MessageStatusListenerService implements IMessageStatusListenerService {
    private final IMessageService messageService;
    private final IMessagingProviderService messagingProviderService;
    private static final List<MessageStatus> NON_TERMINAL_STATUSES = List.of
            (MessageStatus.QUEUED, MessageStatus.SENT, MessageStatus.ACCEPTED, MessageStatus.SCHEDULED);


    /**
     * This method is used to update the status of an SMS.
     * @param smsStatus the object containing the ID and status of the SMS
     */
    @Override
    public void onMessageStatusChanged(IMessageStatusCallback smsStatus) {
        log.info("Updating SMS status: {}", smsStatus);
        try {
            messageService.updateMessageStatus(
                    smsStatus.getMessageId(),
                    MessageStatus.fromString(smsStatus.getStatus()),
                    smsStatus.getErrorCode().orElse(null),
                    smsStatus.getErrorMessage().orElse(null)
            );

        } catch (Exception e) {
            log.error("Failed to update SMS status: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    /**
     * This method is used to poll the status of SMS messages.
     * It fetches all non-terminal messages from the database and updates their status.
     */
    @Override
    @Async("threadPoolTaskExecutor")
    @Scheduled(fixedDelay = 10000, timeUnit = TimeUnit.MILLISECONDS, initialDelay = 10000)
    public void pollMessageStatus() {
        if (messagingProviderService.pollMessageStatus()) {
            log.info("Polling SMS status");
            List<MessageResponseDto> nonTerminalMessages = messageService.getAllMessagesByStatus(NON_TERMINAL_STATUSES);
            log.info("Found {} Non-terminal messages: {}", nonTerminalMessages.size(), nonTerminalMessages);
            for (MessageResponseDto messageResponse : nonTerminalMessages) {
                try {
                    IMessageWrapper polledMessage = messagingProviderService.fetchMessageById(messageResponse.id());
                    if (messageResponse.status() != polledMessage.getStatus()) {
                        messageService.updateMessageStatus(
                                polledMessage.getId(),
                                polledMessage.getStatus(),
                                polledMessage.getErrorCode().orElse(null),
                                polledMessage.getErrorMessage().orElse(null)
                        );
                    }
                } catch (Exception e) {
                    log.error("Failed to poll SMS status: {}", e.getMessage());
                }
            }
        }
    }
}
