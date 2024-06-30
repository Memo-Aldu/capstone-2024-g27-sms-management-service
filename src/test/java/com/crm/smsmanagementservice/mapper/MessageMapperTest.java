package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/18/2024, Monday
 */
public class MessageMapperTest {
  private final MessageMapper messageMapper = new MessageMapperImpl();

  @Test
  public void toDocument_NullMessage_ReturnsNull() {
    assertNull(messageMapper.toDocument(null));
  }

  @Test
  public void toDocument_ValidMessage_MapsCorrectly() {
    // Given
    IMessageWrapper message = mock(IMessageWrapper.class);
    when(message.getServiceSid()).thenReturn(Optional.of("serviceSid"));
    when(message.getErrorCode()).thenReturn(Optional.of("errorCode"));
    when(message.getErrorMessage()).thenReturn(Optional.of("errorMessage"));
    when(message.getSegmentCount()).thenReturn(Optional.of("5"));
    when(message.getMediaCount()).thenReturn(Optional.of("3"));
    when(message.getMediaUrls()).thenReturn(Optional.of(Map.of("key", "value")));
    when(message.getSender()).thenReturn(Optional.of("sender"));
    when(message.getId()).thenReturn("id");
    when(message.getRecipient()).thenReturn("recipient");
    when(message.getMessageContent()).thenReturn("messageContent");
    when(message.getCreatedTime()).thenReturn(ZonedDateTime.now());
    when(message.getDeliveredTime()).thenReturn(ZonedDateTime.now());
    when(message.getScheduledTime()).thenReturn(ZonedDateTime.now());
    when(message.getStatus()).thenReturn(MessageStatus.DELIVERED);
    when(message.getProviderId()).thenReturn("providerId");

    // When
    MessageDocument document = messageMapper.toDocument(message);

    // Then
    assertNotNull(document);
    assertEquals("serviceSid", document.getServiceSid());
    assertEquals("errorCode", document.getErrorCode());
    assertEquals("errorMessage", document.getErrorMessage());
    assertEquals(5, document.getSegmentCount());
    assertEquals(3, document.getMediaCount());
    assertNotNull(document.getMediaUrls());
    assertEquals("value", document.getMediaUrls().get("key"));
    assertEquals("sender", document.getSender());
    assertEquals("id", document.getId());
    assertEquals("recipient", document.getRecipient());
    assertEquals("messageContent", document.getMessageContent());
    assertNotNull(document.getCreatedTime());
    assertNotNull(document.getDeliveredTime());
    assertNotNull(document.getScheduledTime());
    assertEquals(MessageStatus.DELIVERED, document.getStatus());
    assertEquals("providerId", document.getProviderId());
  }

    @Test
    void toResponseDto_NullDocument_ReturnsNull() {
        assertNull(messageMapper.toResponseDto(null));
    }

    @Test
    void toResponseDto_ValidDocument_MapsCorrectly() {
        // Given
        MessageDocument document = MessageDocument.builder()
            .id("MESSAGE_ID")
            .createdTime(ZonedDateTime.now()).messageContent("Hello, how are you?")
            .sender("SENDER").recipient("RECIPIENT").status(MessageStatus.SENT)
            .conversationId("CONVERSATION_ID")
            .build();

        // When
        MessageResponseDto responseDto = messageMapper.toResponseDto(document);

        // Then
        assertNotNull(responseDto);
        assertEquals("MESSAGE_ID", responseDto.id());
        assertEquals("Hello, how are you?", responseDto.messageContent());
        assertEquals(MessageStatus.SENT, responseDto.status());
        assertEquals("CONVERSATION_ID", responseDto.conversationId());
    }

    @Test
    void toResponseDtoList_NullDocument_ReturnsNull() {
        assertNull(messageMapper.toResponseDtoList(null));
    }

    @Test
    void toResponseDtoList_ValidDocument_MapsCorrectly() {
        // Given
        MessageDocument document = MessageDocument.builder()
            .id("MESSAGE_ID")
            .createdTime(ZonedDateTime.now()).messageContent("Hello, how are you?")
            .sender("SENDER").recipient("RECIPIENT").status(MessageStatus.SENT)
            .conversationId("CONVERSATION_ID")
            .build();
        List<MessageDocument> documents = new ArrayList<>();
        documents.add(document);

        // When
        List<MessageResponseDto> responseDtos = messageMapper.toResponseDtoList(documents);

        // Then
        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals("MESSAGE_ID", responseDtos.getFirst().id());
        assertEquals("Hello, how are you?", responseDtos.getFirst().messageContent());
        assertEquals(MessageStatus.SENT, responseDtos.getFirst().status());
        assertEquals("CONVERSATION_ID", responseDtos.getFirst().conversationId());
    }
}
