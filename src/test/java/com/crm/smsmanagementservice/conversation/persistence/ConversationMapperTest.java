package com.crm.smsmanagementservice.conversation.persistence;

import com.crm.smsmanagementservice.conversation.ConversationDTO;
import com.crm.smsmanagementservice.conversation.ConversationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-24, Tuesday
 */
public class ConversationMapperTest {
    private final ConversationMapper conversationMapper = new ConversationMapperImpl();

    @Test
    public void testConversationDocumentToDTO() {
        // Prepare a sample ConversationDocument
        ConversationDocument document = ConversationDocument.builder()
                .id("conv-1")
                .userId("user-1")
                .contactId("contact-1")
                .conversationName("Test Conversation")
                .status(ConversationStatus.OPEN)
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .build();

        // Map to DTO
        ConversationDTO dto = conversationMapper.toDTO(document);

        // Assert that the values are correctly mapped
        assertEquals(document.getId(), dto.id());
        assertEquals(document.getUserId(), dto.userId());
        assertEquals(document.getContactId(), dto.contactId());
        assertEquals(document.getConversationName(), dto.conversationName());
        assertEquals(document.getStatus(), dto.status());
    }

    @Test
    public void testConversationDTOToDocument() {
        // Prepare a sample DTO
        ConversationDTO dto = ConversationDTO.builder()
                .id("conv-1")
                .userId("user-1")
                .contactId("contact-1")
                .conversationName("Test Conversation")
                .status(ConversationStatus.OPEN)
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .build();

        // Map to document
        ConversationDocument document = conversationMapper.toDocument(dto);

        // Assert that the values are correctly mapped
        assertEquals(dto.id(), document.getId());
        assertEquals(dto.userId(), document.getUserId());
        assertEquals(dto.contactId(), document.getContactId());
        assertEquals(dto.conversationName(), document.getConversationName());
        assertEquals(dto.status(), document.getStatus());
    }
}

