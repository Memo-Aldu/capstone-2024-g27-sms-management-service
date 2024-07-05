package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.entity.ConversationDocument;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.mapper.ConversationMapper;
import com.crm.smsmanagementservice.repository.ConversationRepository;
import com.crm.smsmanagementservice.service.conversation.ConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/19/2024, Wednesday
 */
@ExtendWith(MockitoExtension.class)
public class ConversationServiceTest {
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private ConversationMapper conversationMapper;
    @Mock
    private IMessageService messageService;
    @InjectMocks private ConversationService conversationService;


    @Test
    public void whenCreateConversation_thenSuccess() {
        // given
        var request = new ConversationPostRequestDto("1234567890",
                "1234567891", "Test Conversation");
        var conversationDocument = ConversationDocument.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        var conversationResponseDto = ConversationResponseDto.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .id("CONVERSATION_ID")
                .conversationName("Test Conversation")
                .build();
        // when
        when(conversationMapper.toDocument(request)).thenReturn(conversationDocument);
        when(conversationRepository.save(conversationDocument)).thenReturn(conversationDocument);
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(conversationResponseDto);
        var result = conversationService.createConversation(request);
        // then
        assert result != null;
        assert result.sender().equals("1234567890");
        assert result.recipient().equals("1234567891");
        assert result.conversationName().equals("Test Conversation");
    }

    @Test
    public void whenGetConversationById_thenSuccess() {
        // given
        String id = "CONVERSATION_ID";
        var conversationDocument = ConversationDocument.builder()
                .id(id)
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        var conversationResponseDto = ConversationResponseDto.builder()
                .id(id)
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        // when
        when(conversationRepository.findById(id)).thenReturn(java.util.Optional.of(conversationDocument));
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(conversationResponseDto);
        var result = conversationService.getConversationById(id);
        // then
        assert result != null;
        assert result.id().equals(id);
        assert result.sender().equals("1234567890");
        assert result.recipient().equals("1234567891");
        assert result.conversationName().equals("Test Conversation");
    }

    @Test
    public void whenGetConversationByInvalidId_thenShouldTrowException() {
        // given
        String id = "CONVERSATION_ID";
        // when
        when(conversationRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(DomainException.class, () -> conversationService.getConversationById(id));
    }

    @Test
    public void whenGetConversationsByParticipant_thenSuccess() {
        // given
        String participant = "1234567890";
        var conversationDocument = ConversationDocument.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversationDocument> page =
                new PageImpl<>(Collections.singletonList(conversationDocument), pageable, 1);

        // when
        when(conversationRepository.findConversationDocumentByRecipientOrSender(participant, pageable)).thenReturn(page);
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(ConversationResponseDto.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build());
        var result = conversationService.getConversationsByParticipant(participant, pageable);
        // then
        assert result.getTotalElements() == 1;
        assert result.getContent().getFirst().sender().equals("1234567890");
        assert result.getContent().getFirst().recipient().equals("1234567891");
        assert result.getContent().getFirst().conversationName().equals("Test Conversation");
    }

    @Test
    public void whenGetOrCreateConversation_thenSuccess() {
        // given
        String sender = "1234567890";
        String recipient = "1234567891";
        var conversationDocument = ConversationDocument.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();

        // when
        when(conversationRepository.findConversationDocumentBySenderAndRecipient(sender, recipient)).thenReturn(java.util.Optional.of(conversationDocument));
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(ConversationResponseDto.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build());
        var result = conversationService.getOrCreateConversation(sender, recipient);
        // then
        assert result.sender().equals("1234567890");
        assert result.recipient().equals("1234567891");
        assert result.conversationName().equals("Test Conversation");
    }

    @Test
    public void whenGetOrCreateConversation_thenCreateSuccess() {
        // given
        String sender = "1234567890";
        String recipient = "1234567891";
        ConversationDocument conversationDocument = ConversationDocument.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .createdTime(ZonedDateTime.now())
                .updatedTime(ZonedDateTime.now())
                .build();
        // when
        when(conversationRepository.findConversationDocumentBySenderAndRecipient(sender, recipient)).thenReturn(java.util.Optional.empty());
        when(conversationRepository.save(any(ConversationDocument.class))).thenReturn(conversationDocument);
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(ConversationResponseDto.builder()
                .sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build());
        var result = conversationService.getOrCreateConversation(sender, recipient);
        // then
        assert result.sender().equals("1234567890");
        assert result.recipient().equals("1234567891");
        assert result.conversationName().equals("Test Conversation");
    }

    @Test
    public void whenUpdateConversation_thenSuccess() {
        // given
        String id = "CONVERSATION_ID";
        var request = new ConversationPatchRequestDto("New Conversation Name");
        var conversationDocument = ConversationDocument.builder()
                .id(id).sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        var conversationResponseDto = ConversationResponseDto.builder()
                .id(id).sender("1234567890").recipient("1234567891")
                .conversationName("New Conversation Name")
                .build();
        // when
        when(conversationRepository.findById(id)).thenReturn(java.util.Optional.of(conversationDocument));
        when(conversationRepository.save(conversationDocument)).thenReturn(conversationDocument);
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(conversationResponseDto);
        var result = conversationService.updateConversation(id, request);
        // then
        assert result.id().equals(id);
        assert result.conversationName().equals("New Conversation Name");
    }

    @Test
    public void whenUpdateConversationWithInvalidId_thenShouldThrowException() {
        // given
        String id = "CONVERSATION_ID";
        var request = new ConversationPatchRequestDto("New Conversation Name");
        // when
        when(conversationRepository.findById(id)).thenReturn(java.util.Optional.empty());
        // then
        assertThrows(DomainException.class, () -> conversationService.updateConversation(id, request));
    }

    @Test
    public void whenUpdateConversationWithSameName_thenSuccess() {
        // given
        String id = "CONVERSATION_ID";
        var request = new ConversationPatchRequestDto("Test Conversation");
        var conversationDocument = ConversationDocument.builder()
                .id(id).sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        var conversationResponseDto = ConversationResponseDto.builder()
                .id(id).sender("1234567890").recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        // when
        when(conversationRepository.findById(id)).thenReturn(java.util.Optional.of(conversationDocument));
        when(conversationMapper.toConversationResponseDto(conversationDocument)).thenReturn(conversationResponseDto);
        var result = conversationService.updateConversation(id, request);
        // then
        assert result.id().equals(id);
        assert result.conversationName().equals("Test Conversation");

    }

    @Test
    public void whenDeleteConversation_thenSuccess() {
        // given
        String id = "CONVERSATION_ID";
        var conversationDocument = ConversationDocument.builder()
                .id(id).sender("1234567890")
                .recipient("1234567891")
                .conversationName("Test Conversation")
                .build();
        // when
        conversationService.deleteConversation(id);
        // then no exception
        verify(conversationRepository).deleteById(id);

    }
}
