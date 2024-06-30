package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.entity.ConversationDocument;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/18/2024, Monday
 */
public class ConversationMapperTest {
    private final ConversationMapper conversationMapper = new ConversationMapperImpl();

    @Test
    public void toDocument_NullConversation_ReturnsNull() {
        assertNull(conversationMapper.toDocument(null));
    }

    @Test
    public void toDocument_ValidConversation_MapsCorrectly() {
        // Given
        ConversationPostRequestDto conversationPostRequestDto = new ConversationPostRequestDto(
                "SENDER",
                "RECIPIENT",
                "New Conversation"
        );

        // When
        ConversationDocument document = conversationMapper.toDocument(conversationPostRequestDto);

        // Then
        assertNotNull(document);
        assertEquals("SENDER", document.getSender());
        assertEquals("RECIPIENT", document.getRecipient());
        assertEquals("New Conversation", document.getConversationName());
    }

    @Test
    public void toConversationResponseDto_NullConversation_ReturnsNull() {
        assertNull(conversationMapper.toConversationResponseDto(null));
    }

    @Test
    public void toConversationResponseDto_ValidConversation_MapsCorrectly() {
        // Given
        ConversationDocument document = ConversationDocument.builder()
                .id("CONVERSATION_ID")
                .createdTime(ZonedDateTime.now())
                .updatedTime(ZonedDateTime.now())
                .sender("SENDER")
                .recipient("RECIPIENT")
                .conversationName("New Conversation")
                .build();

        // When
        ConversationResponseDto responseDto = conversationMapper.toConversationResponseDto(document);

        // Then
        assertNotNull(responseDto);
        assertEquals("CONVERSATION_ID", responseDto.id());
        assertEquals("SENDER", responseDto.sender());
        assertEquals("RECIPIENT", responseDto.recipient());
        assertEquals("New Conversation", responseDto.conversationName());
    }

}

