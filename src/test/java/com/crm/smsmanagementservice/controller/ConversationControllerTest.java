package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/8/2024, Saturday
 */
public class ConversationControllerTest {
    @InjectMocks
    private ConversationController conversationController;

    @Mock
    private IConversationService conversationService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void shouldGetConversationByIdSuccessfully() {

        when(conversationService.getConversationById("CONVERSATION_ID")).thenReturn(
                new ConversationResponseDto("CONVERSATION_ID", "SENDER", "RECIPIENT",
                        "New Conversation", ZonedDateTime.now(), ZonedDateTime.now())
        );

        ResponseEntity<ConversationResponseDto> response = conversationController.getConversationById("CONVERSATION_ID");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert response.getBody() != null;
        assertEquals("CONVERSATION_ID", response.getBody().id());
        assertEquals("SENDER", response.getBody().sender());
        verify(conversationService, times(1)).getConversationById("CONVERSATION_ID");
    }

    @Test
    public void shouldGetConversationByParticipantSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdTime"));
        ConversationResponseDto conversationResponseDto = new ConversationResponseDto(
                "CONVERSATION_ID", "SENDER", "RECIPIENT",
                "New Conversation", ZonedDateTime.now(), ZonedDateTime.now()
        );

        Page<ConversationResponseDto> conversationResponseDtos = new PageImpl<>(
            Collections.singletonList(conversationResponseDto), pageable, 1
        );

        when(conversationService.getConversationsByParticipant("SENDER", pageable))
                .thenReturn(conversationResponseDtos);

        ResponseEntity<Page<ConversationResponseDto>> response = conversationController
                .getConversationsByParticipant("SENDER", 0, 10, "createdTime", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        assertEquals("CONVERSATION_ID", response.getBody().getContent().getFirst().id());
        verify(conversationService, times(1)).getConversationsByParticipant("SENDER", pageable);
    }

    @Test
    public void shouldCreateConversationSuccessfully() throws Exception {
        ConversationResponseDto conversationResponseDto = new ConversationResponseDto(
                "CONVERSATION_ID", "SENDER", "RECIPIENT",
                "New Conversation", ZonedDateTime.now(), ZonedDateTime.now()
        );

        when(conversationService.createConversation(any())).thenReturn(conversationResponseDto);

    ResponseEntity<ConversationResponseDto> response =
        conversationController.createConversation(
            new ConversationPostRequestDto(
                "SENDER",
                "RECIPIENT",
                "New Conversation"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert response.getBody() != null;
        assertEquals("CONVERSATION_ID", response.getBody().id());
        verify(conversationService, times(1)).createConversation(any());
    }


    @Test
    public void shouldUpdateConversationSuccessfully() {
        ConversationResponseDto conversationResponseDto = new ConversationResponseDto(
                "CONVERSATION_ID", "SENDER", "RECIPIENT",
                "New Conversation", ZonedDateTime.now(), ZonedDateTime.now()
        );

    when(conversationService.updateConversation(
            "CONVERSATION_ID",
            new ConversationPatchRequestDto(
                "New Conversation")))
        .thenReturn(conversationResponseDto);

    ResponseEntity<ConversationResponseDto> response =
        conversationController.updateConversation(
            "CONVERSATION_ID",
            new ConversationPatchRequestDto("New Conversation"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert response.getBody() != null;
        assertEquals("CONVERSATION_ID", response.getBody().id());
        verify(conversationService, times(1)).updateConversation("CONVERSATION_ID", new ConversationPatchRequestDto("New Conversation"));
    }

    @Test
    public void shouldDeleteConversationSuccessfully() {
        doNothing().when(conversationService).deleteConversation("CONVERSATION_ID");

        ResponseEntity<?> response = conversationController.deleteConversation("CONVERSATION_ID");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(conversationService, times(1)).deleteConversation("CONVERSATION_ID");
    }
}
