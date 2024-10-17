package com.crm.smsmanagementservice.conversation.web;

import com.crm.smsmanagementservice.conversation.ConversationDTO;
import com.crm.smsmanagementservice.conversation.ConversationExternalAPI;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationExternalAPI conversationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConversationDTO conversationDTO;

    @BeforeEach
    void setUp() {
        conversationDTO = ConversationDTO.builder()
                .id("conv1")
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();
    }

    @Test
    void testGetConversationById_Success() throws Exception {
        when(conversationService.getConversationById("conv1")).thenReturn(conversationDTO);

        mockMvc.perform(get("/api/v1/conversation/{id}", "conv1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("conv1"))
                .andExpect(jsonPath("$.data.conversationName").value("Test Conversation"))
                .andExpect(jsonPath("$.responseMessage").value("Conversation fetched successfully"));}

    @Test
    void testGetConversationById_NotFound() throws Exception {
        when(conversationService.getConversationById("conv1"))
                .thenThrow(new DomainException(Error.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/conversation/{id}", "conv1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetConversationsByUserId_Success() throws Exception {
        List<ConversationDTO> conversations = Collections.singletonList(conversationDTO);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "updatedDate"));
        Page<ConversationDTO> conversationPage = new PageImpl<>(conversations, pageable, conversations.size());
        when(conversationService.getConversationsByUserId(eq("user1"), any(Pageable.class)))
                .thenReturn(conversationPage);

        mockMvc.perform(get("/api/v1/conversation/user/{id}", "user1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("conv1"))
                .andExpect(jsonPath("$.responseMessage").value("Conversations fetched successfully"));
    }

    @Test
    void testCreateConversation_Success() throws Exception {
        when(conversationService.createConversation(any(ConversationDTO.class))).thenReturn(conversationDTO);

        String json = """
        {
            "conversationName": "Test Conversation",
            "contactId": "contact1",
            "userId": "user1"
        }
        """;

        mockMvc.perform(post("/api/v1/conversation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value("conv1"))
                .andExpect(jsonPath("$.responseMessage").value("Conversation created successfully"));
    }

    @Test
    void testCreateConversation_InvalidRequest() throws Exception {
        String json = """
        {
            "conversationName": "Test Conversation",
            "contactId": null,
            "userId": null
        }
        """;

        mockMvc.perform(post("/api/v1/conversation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateConversationStatus_Success() throws Exception {
        when(conversationService.updateConversation(any(), any())).thenReturn(conversationDTO);

        String json = """
        {
            "status": "CLOSED"
        }
        """;

        mockMvc.perform(patch("/api/v1/conversation/{id}", "conv1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value("Conversation updated successfully"));
    }

    @Test
    void testUpdateConversationName_Success() throws Exception {
        when(conversationService.updateConversation(any(), any())).thenReturn(conversationDTO);

        String json = """
        {
            "conversationName": "Updated Conversation"
        }
        """;

        mockMvc.perform(patch("/api/v1/conversation/{id}", "conv1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseMessage").value("Conversation updated successfully"));
    }

    @Test
    void testUpdateConversation_InvalidRequest() throws Exception {
        String json = """
        {
            "conversationName": null,
            "status": null
        }
        """;

        mockMvc.perform(patch("/api/v1/conversation/{id}", "conv1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}