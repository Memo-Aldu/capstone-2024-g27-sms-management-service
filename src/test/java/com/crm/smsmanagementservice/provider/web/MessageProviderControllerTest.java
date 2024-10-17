package com.crm.smsmanagementservice.provider.web;

import com.crm.smsmanagementservice.provider.MessagingProviderExternalAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageProviderController.class)
public class MessageProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingProviderExternalAPI messagingProvider;

    @BeforeEach
    void setup() {
        Mockito.reset(messagingProvider);
    }

    @Test
    void testHandleCallback_Success() throws Exception {
        mockMvc.perform(post("/api/v1/provider/callback/message-status")
                        .param("MessageSid", "SM12345")
                        .param("AccountSid", "AC12345")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(messagingProvider, times(1)).handleIncomingMessageStatusUpdate(any());
    }

    @Test
    void testHandleCallback_MissingRequiredParams() throws Exception {
        // Missing required parameters "MessageSid" and "AccountSid"
        mockMvc.perform(post("/api/v1/provider/callback/message-status")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHandleInboundMessage_Success() throws Exception {
        mockMvc.perform(post("/api/v1/provider/callback/inbound-message")
                        .param("MessageSid", "SM12345")
                        .param("AccountSid", "AC12345")
                        .param("From", "+1234567890")
                        .param("To", "+0987654321")
                        .param("Body", "Hello World")
                        .param("NumMedia", "2")
                        .param("MediaContentType", "image/jpeg")
                        .param("MediaContentType", "image/png")
                        .param("MediaUrl", "https://example.com/image1.jpg")
                        .param("MediaUrl", "https://example.com/image2.png")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(messagingProvider, times(1)).handleIncomingMessage(any());
    }

    @Test
    void testHandleInboundMessage_MissingRequiredParams() throws Exception {
        // Missing "MessageSid" and "AccountSid"
        mockMvc.perform(post("/api/v1/provider/callback/inbound-message")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }
}