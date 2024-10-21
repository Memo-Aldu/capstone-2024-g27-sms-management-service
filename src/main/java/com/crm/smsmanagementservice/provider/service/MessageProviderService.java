package com.crm.smsmanagementservice.provider.service;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.event.DeliveredMessageEvent;
import com.crm.smsmanagementservice.core.event.InboundMessageEvent;
import com.crm.smsmanagementservice.core.event.UpdateMessageEvent;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.provider.MessagingProviderExternalAPI;
import com.crm.smsmanagementservice.provider.MessagingProviderInternalAPI;
import com.crm.smsmanagementservice.provider.ProviderMessagingDTO;
import com.crm.smsmanagementservice.provider.web.InboundMessageDTO;
import com.crm.smsmanagementservice.provider.web.MessageStatusUpdateDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
@RequiredArgsConstructor @Service
@Slf4j(topic = "MESSAGE_PROVIDER_SERVICE") @Transactional
public class MessageProviderService implements MessagingProviderInternalAPI, MessagingProviderExternalAPI {
    private final @NonNull MessagingClient messagingClient;
    private final @NonNull ApplicationEventPublisher applicationEventPublisher;

    @NonNull
    @Override
    public Map<String, DomainMessage> sendMessages(@NonNull ProviderMessagingDTO messages) {
        boolean isMMS = this.isMMS(messages);
        if (isMMS) {
            log.info("Sending {} MMS messages", messages.messageItems().size());
            return messages.messageItems().entrySet().stream().parallel().collect(
                    Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.sendMMSFromNumber(
                            entry.getValue().recipient(),
                            entry.getValue().sender(),
                            entry.getValue().content(),
                            messages.media()
                    )
            ));
        }
        log.info("Sending {} SMS messages", messages.messageItems().size());
        return messages.messageItems().entrySet().stream().parallel().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.sendSMSFromNumber(
                        entry.getValue().recipient(),
                        entry.getValue().sender(),
                        entry.getValue().content()
                )
        ));
    }

    @Override @NonNull
    public Map<String, DomainMessage> scheduleMessages(@NonNull ProviderMessagingDTO messages) {
        if (messages.scheduledDate() == null) {
            throw new DomainException(Error.INVALID_REQUEST);
        }
        boolean isMMS = this.isMMS(messages);
        if (isMMS) {
            log.info("Scheduling {} MMS messages", messages.messageItems().size());
            return messages.messageItems().entrySet().stream().parallel().collect(
                    Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.scheduleMMS(
                            entry.getValue().recipient(),
                            entry.getValue().content(),
                            messages.media(),
                            messages.scheduledDate()
                    )
            ));
        }
        log.info("Scheduling {} SMS messages", messages.messageItems().size());
        return messages.messageItems().entrySet().stream().parallel().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.scheduleSMS(
                        entry.getValue().recipient(),
                        entry.getValue().content(),
                        messages.scheduledDate()
                )
        ));
    }

    @Override @NonNull
    public Map<String, DomainMessage> bulkSendMessages(@NonNull ProviderMessagingDTO messages) {
        boolean isMMS = this.isMMS(messages);
        if (isMMS) {
            log.info("Bulk sending {} MMS messages", messages.messageItems().size());
            return messages.messageItems().entrySet().stream().parallel().collect(
                    Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.sendMMSFromService(
                            entry.getValue().recipient(),
                            entry.getValue().content(),
                            messages.media()
                    )
            ));
        }
        log.info("Bulk sending {} SMS messages", messages.messageItems().size());
        return messages.messageItems().entrySet().stream().parallel().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> messagingClient.sendSMSFromService(
                        entry.getValue().recipient(),
                        entry.getValue().content()
                )
        ));
    }

    @Override
    public boolean cancelMessage(@NonNull String resourceId) {
        log.info("Cancelling message with resourceId: {}", resourceId);
        return messagingClient.cancelMessage(resourceId).getStatus().equals(MessageStatus.CANCELED);
    }

    @Override
    public void handleIncomingMessage(InboundMessageDTO message) {
        DomainMessage domainMessage = messagingClient.fetchMessageById(message.messageId());
        InboundMessageEvent inboundMessageEvent = new InboundMessageEvent(this, domainMessage);
        log.info("Publishing inbound message event");
        applicationEventPublisher.publishEvent(inboundMessageEvent);
    }

    @Override
    public void handleIncomingMessageStatusUpdate(MessageStatusUpdateDTO message) {
        log.info("Received message status update: {}, status: {}", message.messageId(), message.messageStatus());
        MessageStatus messageStatus = MessageStatus.fromString(message.messageStatus());
        if (messageStatus == MessageStatus.DELIVERED) {
            log.info("Message delivered: {}", message.messageId());
            DomainMessage domainMessage = messagingClient.fetchMessageById(message.messageId());
            DeliveredMessageEvent deliveredMessageEvent = new DeliveredMessageEvent(this, domainMessage);
            log.info("Publishing delivered message event");
            applicationEventPublisher.publishEvent(deliveredMessageEvent);
        } else {
            UpdateMessageEvent updateMessageEvent = new UpdateMessageEvent(
                    this, message.messageId(), message.accountId(), message.serviceId(),
                    message.errorCode(), message.errorMessage(), messageStatus
            );
            log.info("Publishing message status update event");
            applicationEventPublisher.publishEvent(updateMessageEvent);
        }
    }

    private boolean isMMS(ProviderMessagingDTO messages) {
        return messages.media() != null && !messages.media().isEmpty();
    }
}
