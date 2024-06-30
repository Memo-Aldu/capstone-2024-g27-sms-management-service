package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.config.EmbeddedMongoConfig;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.mapper.ConversationMapperImpl;
import com.crm.smsmanagementservice.mapper.MessageMapperImpl;
import com.crm.smsmanagementservice.repository.ConversationRepository;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.conversation.ConversationService;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertThrows;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/19/2024, Wednesday
 */
@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class) @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoConfig.class) @RequiredArgsConstructor
public class ConversationServiceIT {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;

    private IMessageService messageService;
    private IConversationService conversationService;

    @BeforeAll
    public void init() {
        messageService = new MessageService(messageRepository, new MessageMapperImpl());
        conversationService = new ConversationService(conversationRepository, new ConversationMapperImpl(), messageService);
    }

    @BeforeEach
    public void setup() {
        conversationRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    public void whenCreateConversation_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        assert responseDto.conversationName().equals("CONVERSATION_NAME");
        assert responseDto.sender().equals("1234567890");
        assert responseDto.recipient().equals("8192224444");
    }

    @Test
    public void whenGetConversationById_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        ConversationResponseDto fetchedDto = conversationService.getConversationById(responseDto.id());
        assert fetchedDto != null;
        assert fetchedDto.id().equals(responseDto.id());
    }

    @Test
    public void whenGetConversationByInvalidId_thenShouldTrowException() {
        assertThrows(DomainException.class, () -> conversationService.getConversationById("INVALID_ID"));
    }

    @Test
    public void whenGetConversationsByParticipant_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        assert conversationService.getConversationsByParticipant("1234567890", null).getTotalElements() == 1;
    }

    @Test
    public void whenGetOrCreateConversation_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        ConversationResponseDto fetchedDto = conversationService.getOrCreateConversation("1234567890", "8192224444");
        assert fetchedDto != null;
        assert fetchedDto.id().equals(responseDto.id());
    }

    @Test
    public void whenUpdateConversation_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        ConversationPatchRequestDto patchRequestDto = new ConversationPatchRequestDto(
                "UPDATED_CONVERSATION_NAME");
        ConversationResponseDto updatedDto = conversationService.updateConversation(responseDto.id(), patchRequestDto);
        assert updatedDto != null;
        assert updatedDto.id().equals(responseDto.id());
        assert updatedDto.conversationName().equals("UPDATED_CONVERSATION_NAME");
    }

    @Test
    public void whenUpdateConversationWithInvalidId_thenShouldThrowException() {
        ConversationPatchRequestDto patchRequestDto = new ConversationPatchRequestDto(
                "UPDATED_CONVERSATION_NAME");
        assertThrows(DomainException.class, () -> conversationService.updateConversation("INVALID_ID", patchRequestDto));
    }

    @Test
    public void whenUpdateConversationWithSameName_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        ConversationPatchRequestDto patchRequestDto = new ConversationPatchRequestDto(
                "CONVERSATION_NAME");
        ConversationResponseDto updatedDto = conversationService.updateConversation(responseDto.id(), patchRequestDto);
        assert updatedDto != null;
        assert updatedDto.id().equals(responseDto.id());
        assert updatedDto.conversationName().equals("CONVERSATION_NAME");
    }

    @Test
    public void whenDeleteConversation_thenSuccess() {
        ConversationPostRequestDto requestDto = new ConversationPostRequestDto(
                "1234567890", "8192224444", "CONVERSATION_NAME");
        ConversationResponseDto responseDto = conversationService.createConversation(requestDto);
        assert responseDto != null;
        conversationService.deleteConversation(responseDto.id());
        assertThrows(DomainException.class, () -> conversationService.getConversationById(responseDto.id()));
    }

}
