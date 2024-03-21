package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
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

public class MessageDocumentMapperTest {
  private final MessageDocumentMapper mapper = new MessageDocumentMapperImpl();
  @Test
  public void toDocument_NullMessage_ReturnsNull() {
    assertNull(mapper.toDocument(null));
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
    SmSDocument document = mapper.toDocument(message);

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
}
