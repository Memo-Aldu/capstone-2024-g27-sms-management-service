package com.crm.smsmanagementservice.message.service;

import com.crm.smsmanagementservice.conversation.ConversationInternalAPI;
import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.message.MessageDTO;
import com.crm.smsmanagementservice.message.MessageExternalAPI;
import com.crm.smsmanagementservice.message.MessageInternalAPI;
import com.crm.smsmanagementservice.message.MessageType;
import com.crm.smsmanagementservice.message.persistence.MessageDocument;
import com.crm.smsmanagementservice.message.persistence.MessageMapper;
import com.crm.smsmanagementservice.message.persistence.MessageRepository;
import com.crm.smsmanagementservice.provider.MessagingProviderInternalAPI;
import com.crm.smsmanagementservice.provider.ProviderMessagingDTO;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
@Slf4j(topic = "MESSAGE_SERVICE")
@Service @RequiredArgsConstructor @NonNullApi
public class MessageService implements MessageExternalAPI, MessageInternalAPI {
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final MessagingProviderInternalAPI messagingProvider;
    private final ConversationInternalAPI conversationInternalAPI;

    @Override
    public MessageDTO getMessageById(String messageId) {
        log.info("Fetching message with id: {}", messageId);
        MessageDocument messageDocument = messageRepository.findById(messageId)
                .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
        log.info("Fetched message with id: {}", messageId);
        return messageMapper.toDTO(messageDocument);
    }

    @Override
    public Page<MessageDTO> getMessageByParticipantId(String userId, String contactId, Pageable pageable) {
        Page<MessageDocument> messageDocuments = messageRepository.findDeliveredMessagesByUserIdAndContactId(userId, contactId,pageable);
        log.info("Fetched messages {} with userId: {} and contactId: {}", messageDocuments.getContent().size(), userId, contactId);
        return messageDocuments.map(messageMapper::toDTO);
    }

    @Override
    public List<MessageDTO> createMessage(List<MessageDTO> messageCreateDTO) {
        boolean isScheduled = messageCreateDTO.getFirst().scheduledDate() != null;
        boolean isBatch = messageCreateDTO.size() > 10;
        Map<String, ProviderMessagingDTO.MessageItemDTO> messageItemDTO = messageCreateDTO
                .stream().parallel().collect(Collectors.toMap(
                        messageDto -> messageDto.userId() + messageDto.contactId(),
                        messageDTO -> new ProviderMessagingDTO.MessageItemDTO(
                                messageDTO.content(),
                                messageDTO.to(),
                                messageDTO.from()
                        )
                ));

        Map<String, DomainMessage> response;
        if (isScheduled) {
            response = messagingProvider.scheduleMessages(
                    ProviderMessagingDTO.builder()
                            .messageItems(messageItemDTO)
                            .media(messageCreateDTO.getFirst().media())
                            .scheduledDate(messageCreateDTO.getFirst().scheduledDate())
                            .build());
        } else if (isBatch) {
            response = messagingProvider.bulkSendMessages(
                    ProviderMessagingDTO.builder()
                            .messageItems(messageItemDTO)
                            .media(messageCreateDTO.getFirst().media())
                            .build());
        } else {
            response = messagingProvider.sendMessages(
                    ProviderMessagingDTO.builder()
                            .messageItems(messageItemDTO)
                            .media(messageCreateDTO.getFirst().media())
                            .build());
        }
        log.info("Created messages: {}", response);
        Map<String, MessageDocument> messageDocuments = response.entrySet()
                .stream().parallel().collect(
                Collectors.toMap(Map.Entry::getKey, entry ->
                        messageMapper.toDocument(entry.getValue()))
        );

        List<MessageDocument> savedMessages =  messageRepository.saveAll(setFields(messageCreateDTO, messageDocuments));

        return savedMessages.stream().map(messageMapper::toDTO).toList();
    }

    @Override
    public Page<MessageDTO> getMessagesByUserId(String userId, Pageable pageable) {
        Page<MessageDocument> messageDocuments = messageRepository.findMessageDocumentByUserId(userId, pageable);
        log.info("Fetched messages {} with userId: {}", messageDocuments.getContent().size(), userId);
        return messageDocuments.map(messageMapper::toDTO);
    }

    @Override
    public MessageDTO cancelMessage(String messageId) {
        MessageDocument messageDocument = messageRepository.findById(messageId)
                .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
        if (messageDocument.isCancelled()) {
            throw new DomainException(Error.INVALID_REQUEST, "Message is already cancelled");
        }
        if (!messageDocument.canCancel()) {
            throw new DomainException(Error.INVALID_REQUEST, "Message cannot be cancelled, status: " + messageDocument.getStatus());
        }
        if (!messagingProvider.cancelMessage(messageDocument.getResourceId())) {
            throw new DomainException(Error.UNEXPECTED_ERROR, "Failed to cancel message");
        }
        messageDocument.setStatus(MessageStatus.CANCELLED);
        return messageMapper.toDTO(messageRepository.save(messageDocument));
    }

    @Override
    public void updateMessageStatus(String messageId, MessageStatus status, String errorMessage, String errorCode) {
        log.info("Updating message status with id: {} to: {} with error: {} and code: {}", messageId, status, errorMessage, errorCode);
        MessageDocument messageDocument = messageRepository.findByResourceId(messageId)
                .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
        if (messageDocument.getStatus().equals(status)) {
            log.info("Message status with id: {} is already: {}", messageId, status);
            return;
        }
        messageDocument.setStatus(status);
        messageDocument.setErrorCode(errorCode);
        messageDocument.setErrorMessage(errorMessage);
        messageRepository.save(messageDocument);
    }

    @Override
    public void updateMessage(DomainMessage message) {
        log.info("Updating message: {}", message);
        MessageDocument messageDocument = messageRepository.findByResourceId(message.getId())
                .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
        if (!messageDocument.getStatus().equals(message.getStatus())) {
            messageDocument.setStatus(message.getStatus());
        }
        if (message.getErrorCode().isPresent()) {
            messageDocument.setErrorCode(message.getErrorCode().get());
        }
        if (message.getErrorMessage().isPresent()) {
            messageDocument.setErrorMessage(message.getErrorMessage().get());
        }
        if (message.getDeliveredTime() != null) {
            messageDocument.setDeliveredTime(message.getDeliveredTime());
        }
        if(message.getPrice() != null) {
            messageDocument.setPrice(message.getPrice().abs());
        }
        if(message.getMediaUrls().isPresent() && message.getMediaUrls().get() != messageDocument.getMedia()) {
            messageDocument.setMedia(message.getMediaUrls().get());
        }

        if(message.getSender().isPresent() && message.getSender().get() != messageDocument.getFrom()) {
            messageDocument.setFrom(message.getSender().get());
        }

        messageRepository.save(messageDocument);
    }

    @Override
    public void createInboundMessage(DomainMessage message) {
        log.info("Adding message: {}", message);
        boolean isMedia = message.getMediaUrls().isPresent();
        MessageDocument messageDocument = messageMapper.toDocument(message);
        String sender = message.getSender().orElseThrow(
                () -> new DomainException(Error.INVALID_REQUEST, "Sender is required"));
        Optional<MessageDocument> previousMessage = messageRepository
                .findFirstByToAndDirectionOrderByCreatedDateDesc(sender, MessageDirection.OUTBOUND_API);

        if (previousMessage.isPresent()) {
            log.info("Found previous message: {}", previousMessage.get().getId());
            messageDocument.setConversationId(previousMessage.get().getConversationId());
            messageDocument.setUserId(previousMessage.get().getUserId());
            messageDocument.setContactId(previousMessage.get().getContactId());
        } else {
            log.warn("No previous message found for sender: {}", sender);
        }

        if (message.getStatus().equals(MessageStatus.RECEIVING)) {
            messageDocument.setStatus(MessageStatus.RECEIVED);
        }
        ZonedDateTime now = ZonedDateTime.now();
        messageDocument.setCreatedDate(now);
        messageDocument.setDeliveredTime(now);

        messageDocument.setType(isMedia ? MessageType.MMS : MessageType.SMS);
        messageRepository.save(messageDocument);
    }

    private List<MessageDocument> setFields(List<MessageDTO> messageCreateDTO, Map<String, MessageDocument> messageDocuments) {
        ZonedDateTime now = ZonedDateTime.now();
        return messageCreateDTO.stream().parallel().map(messageDTO -> {
            MessageDocument messageDocument = messageDocuments.get(messageDTO.userId() + messageDTO.contactId());
            messageDocument.setUserId(messageDTO.userId());
            messageDocument.setContactId(messageDTO.contactId());
            messageDocument.setConversationId(conversationInternalAPI
                    .findOrCreateConversation(messageDTO.userId(), messageDTO.contactId()));
            messageDocument.setCreatedDate(now);
            messageDocument.setDeliveredTime(now);
            messageDocument.setScheduledDate(messageDTO.scheduledDate());
            messageDocument.setType(messageDTO.type());
            return messageDocument;
        }).toList();
    }
}
