package com.crm.smsmanagementservice.message.web;

import com.crm.smsmanagementservice.core.validator.IPhoneNumberValidator;
import com.crm.smsmanagementservice.message.MessageDTO;
import com.crm.smsmanagementservice.message.MessageExternalAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageExternalAPI messageService;

    @MockBean
    private IPhoneNumberValidator phoneNumberValidator;

    private MessageDTO messageDTO;

    @BeforeEach
    void setup() {
        messageDTO = MessageDTO.builder()
                .id("message-id-1")
                .userId("user1")
                .contactId("contact1")
                .content("Hello World")
                .build();
        when(phoneNumberValidator.isValid(anyString(), any())).thenReturn(true);

    }

    @Test
    void testGetMessageById() throws Exception {
        Mockito.when(messageService.getMessageById("message-id-1")).thenReturn(messageDTO);

        mockMvc.perform(get("/api/v1/messages/{id}", "message-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("message-id-1"))
                .andExpect(jsonPath("$.data.content").value("Hello World"))
                .andExpect(jsonPath("$.responseMessage").value("Message fetched successfully"));

        verify(messageService, times(1)).getMessageById("message-id-1");
    }

    @Test
    void testGetMessages() throws Exception {
        List<MessageDTO> messages = Collections.singletonList(messageDTO);
        Mockito.when(messageService.getMessageByParticipantId(eq("user1"), eq("contact1"), any()))
                .thenReturn(new PageImpl<>(messages));

        mockMvc.perform(get("/api/v1/messages")
                        .param("userId", "user1")
                        .param("contactId", "contact1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("message-id-1"))
                .andExpect(jsonPath("$.responseMessage").value("Messages fetched successfully"));

        verify(messageService, times(1)).getMessageByParticipantId(eq("user1"), eq("contact1"), any());
    }

    @Test
    void testCreateSMSMessage() throws Exception {
        List<MessageDTO> messages = Collections.singletonList(messageDTO);
        Mockito.when(messageService.createMessage(any())).thenReturn(messages);

        String requestBody = """
                {
                    "userId": "user1",
                    "from": "+1234567890",
                    "messageItems": [
                        {
                            "contactId": "contact1",
                            "to": "+9876543210",
                            "content": "Hello World"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0].id").value("message-id-1"))
                .andExpect(jsonPath("$.responseMessage").value("Messages created successfully"));

        verify(messageService, times(1)).createMessage(any());
    }

    @Test
    void testCreateMMSMessage() throws Exception {
        List<MessageDTO> messages = Collections.singletonList(messageDTO);
        Mockito.when(messageService.createMessage(any())).thenReturn(messages);

        String requestBody = """
                {
                    "userId": "user1",
                    "from": "+1234567890",
                    "messageItems": [
                        {
                            "contactId": "contact1",
                            "to": "+9876543210",
                            "content": "Hello World"
                        }
                    ],
                    "media": [
                        "http://example.com/image.jpg"
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0].id").value("message-id-1"))
                .andExpect(jsonPath("$.responseMessage").value("Messages created successfully"));

        verify(messageService, times(1)).createMessage(any());
    }

    @Test
    void testUpdateMessage() throws Exception {
        Mockito.when(messageService.cancelMessage("message-id-1")).thenReturn(messageDTO);

        mockMvc.perform(patch("/api/v1/messages/{id}", "message-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("message-id-1"))
                .andExpect(jsonPath("$.responseMessage").value("Message Canceled successfully"));

        verify(messageService, times(1)).cancelMessage("message-id-1");
    }
}
