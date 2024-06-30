package com.crm.smsmanagementservice.service.conversation;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.entity.ConversationDocument;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.ConversationMapper;
import com.crm.smsmanagementservice.repository.ConversationRepository;
import com.crm.smsmanagementservice.service.message.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */

@Service @RequiredArgsConstructor @Slf4j(topic = "CONVERSATION_SERVICE")
public class ConversationService implements IConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final IMessageService messageService;

    @Override
    public ConversationResponseDto createConversation(ConversationPostRequestDto requestDto) {
        log.info("Creating conversation with name: {}", requestDto.conversationName());
        ZonedDateTime createdTime = ZonedDateTime.now();
        ConversationDocument conversationDocument = conversationMapper.toDocument(requestDto);
        conversationDocument.setCreatedTime(createdTime);
        conversationDocument.setUpdatedTime(createdTime);
        conversationDocument = conversationRepository.save(conversationDocument);
        log.info("Conversation created with id: {}", conversationDocument.getId());
        return conversationMapper.toConversationResponseDto(conversationDocument);
    }

    @Override
    public ConversationResponseDto getConversationById(String id) {
        log.info("Fetching conversation with id: {}", id);
        ConversationDocument conversationDocument = conversationRepository.findById(id).orElseThrow(
            () -> new DomainException(Error.ENTITY_NOT_FOUND)
        );
        log.info("Conversation fetched with id: {}", conversationDocument.getId());
        return conversationMapper.toConversationResponseDto(conversationDocument);
    }

    @Override
    public Page<ConversationResponseDto> getConversationsByParticipant(String participant, Pageable pageable) {
        log.info("Fetching conversations by participant: {}", participant);
        return conversationRepository.findConversationDocumentByRecipientOrSender(participant, pageable)
            .map(conversationMapper::toConversationResponseDto);
    }

    @Override
    public ConversationResponseDto getOrCreateConversation(String sender, String recipient) {
        log.info("Fetching conversation between participants: {} and {}", sender, recipient);
        ConversationDocument conversationDocument = conversationRepository
                .findConversationDocumentBySenderAndRecipient(sender, recipient)
            .orElseGet(() -> {
                log.info("No conversation found between participants: {} and {}. Creating new conversation", sender, recipient);
                ZonedDateTime createdTime = ZonedDateTime.now();
                ConversationDocument newConversationDocument = ConversationDocument.builder()
                    .sender(sender)
                    .recipient(recipient)
                    .createdTime(createdTime)
                    .updatedTime(createdTime)
                    .build();
                    return conversationRepository.save(newConversationDocument);
            });
        log.info("Conversation fetched with id: {}", conversationDocument.getId());
        return conversationMapper.toConversationResponseDto(conversationDocument);
    }

    @Override
    public ConversationResponseDto updateConversation(String id, ConversationPatchRequestDto requestDto) {
        log.info("Updating conversation with id: {}", id);
        ConversationDocument conversationDocument = conversationRepository.findById(id).orElseThrow(
            () -> new DomainException(Error.ENTITY_NOT_FOUND)
        );
        if (Objects.equals(requestDto.conversationName(), conversationDocument.getConversationName())) {
            log.info("No changes detected in conversation with id: {}", id);
            return conversationMapper.toConversationResponseDto(conversationDocument);
        }
        conversationDocument.setUpdatedTime(ZonedDateTime.now());
        conversationDocument.setConversationName(requestDto.conversationName());
        conversationDocument = conversationRepository.save(conversationDocument);
        log.info("Conversation updated with id: {}", conversationDocument.getId());
        return conversationMapper.toConversationResponseDto(conversationDocument);
    }

    @Override
    public void deleteConversation(String conversationId) {
        log.info("Deleting conversation with id: {}", conversationId);
        conversationRepository.deleteById(conversationId);
    }
}
