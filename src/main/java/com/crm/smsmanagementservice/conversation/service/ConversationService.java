package com.crm.smsmanagementservice.conversation.service;


import java.time.ZonedDateTime;
import java.util.Objects;

import com.crm.smsmanagementservice.conversation.ConversationDTO;
import com.crm.smsmanagementservice.conversation.ConversationExternalAPI;
import com.crm.smsmanagementservice.conversation.ConversationInternalAPI;
import com.crm.smsmanagementservice.conversation.ConversationStatus;
import com.crm.smsmanagementservice.conversation.persistence.*;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.message.persistence.MessageDocument;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */

@Service @RequiredArgsConstructor @Slf4j(topic = "CONVERSATION_SERVICE") @NonNullApi
public class ConversationService implements ConversationExternalAPI, ConversationInternalAPI {
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;

    @Override
    public ConversationDTO createConversation(ConversationDTO requestDto) {
        log.info("Creating conversation with name: {}", requestDto.conversationName());
        conversationRepository.findByUserIdAndContactId(requestDto.userId(), requestDto.contactId()).ifPresent(
            conversationDocument -> {
                throw new DomainException(Error.ENTITY_ALREADY_EXISTS);
            });
        return conversationMapper.toDTO(saveConversation(requestDto));
    }

    @Override
    public ConversationDTO getConversationById(String id) {
        log.info("Fetching conversation with id: {}", id);
        ConversationDocument conversationDocument = conversationRepository.findById(id).orElseThrow(
            () -> new DomainException(Error.ENTITY_NOT_FOUND)
        );
        log.info("Conversation fetched with id: {}", conversationDocument.getId());
        return conversationMapper.toDTO(conversationDocument);
    }

    @Override
    public Page<ConversationDTO> getConversationsByUserId(String userId, Pageable pageable) {
        log.info("Fetching conversations by user id: {}", userId);
        Page<ConversationDocument> conversationDocuments = conversationRepository.findAllByUserId(userId, pageable);
        if (conversationDocuments.hasContent()) {
            log.info("Conversations fetched by user id: {}", userId);
            return conversationDocuments.map(conversationMapper::toDTO);
        }
        log.info("No conversations found by user id: {}", userId);
        return Page.empty();
    }

    @Override
    public ConversationDTO updateConversation(String id, ConversationDTO conversation) {
        log.info("Updating conversation with id: {}", id);
        ConversationDocument conversationDocument = conversationRepository.findById(id).orElseThrow(
            () -> new DomainException(Error.ENTITY_NOT_FOUND)
        );

        if (conversation.conversationName() != null && !Objects.equals(conversation.conversationName(), conversationDocument.getConversationName())) {
            conversationDocument.setConversationName(conversation.conversationName());
        }

        if(conversation.status() != null && !conversation.status().equals(conversationDocument.getStatus())){
            conversationDocument.setStatus(conversation.status());
        }
        conversationDocument.setUpdatedDate(ZonedDateTime.now());
        return conversationMapper.toDTO(conversationRepository.save(conversationDocument));
    }

    @Override
    public String findOrCreateConversation(String userId, String contactId) {
        return conversationRepository.findByUserIdAndContactId(userId, contactId).map(ConversationDocument::getId).orElseGet(
            () -> saveConversation(ConversationDTO.builder().userId(userId).contactId(contactId).build()).getId()
        );
    }

    private ConversationDocument saveConversation(ConversationDTO conversationDTO) {
        ZonedDateTime createdTime = ZonedDateTime.now();
        ConversationDocument conversationDocument = conversationMapper.toDocument(conversationDTO);
        conversationDocument.setCreatedDate(createdTime);
        conversationDocument.setCreatedDate(createdTime);
        conversationDocument.setStatus(ConversationStatus.OPEN);
        return conversationRepository.save(conversationDocument);
    }
}
