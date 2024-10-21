package com.crm.smsmanagementservice.message.persistence;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.message.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

class MessageMapperTest {

    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        messageMapper = Mappers.getMapper(MessageMapper.class);
    }

    @Test
    void testToDTO() {
        // Create a sample MessageDocument
        Map<String, String> mediaMap = new HashMap<>();
        mediaMap.put("image", "https://example.com/image.jpg");
        mediaMap.put("video", "https://example.com/video.mp4");

        MessageDocument document = MessageDocument.builder()
                .resourceId("msg1")
                .from("+1234567890")
                .to("+0987654321")
                .content("Hello World")
                .media(mediaMap)
                .build();
        document.setMedia(mediaMap);

        // Map to DTO
        MessageDTO dto = messageMapper.toDTO(document);

        // Verify DTO fields
        assertEquals(document.getId(), dto.id());
        assertEquals(document.getFrom(), dto.from());
        assertEquals(document.getTo(), dto.to());
        assertEquals(document.getContent(), dto.content());
        assertEquals(List.of("https://example.com/image.jpg", "https://example.com/video.mp4"), dto.media());
    }

    @Test
    void testToDocument() {
        // Create a sample DomainMessage
        Map<String, String> mediaMap = new HashMap<>();
        mediaMap.put("image", "https://example.com/image.jpg");
        Optional<Map<String, String>> mediaOptional = Optional.of(mediaMap);

        DomainMessage domainMessage = mock(DomainMessage.class);
        when(domainMessage.getId()).thenReturn("msg1");
        when(domainMessage.getSender()).thenReturn(Optional.of("+1234567890"));
        when(domainMessage.getRecipient()).thenReturn("+0987654321");
        when(domainMessage.getMessageContent()).thenReturn("Hello World");
        when(domainMessage.getMediaUrls()).thenReturn(mediaOptional);
        when(domainMessage.getPrice()).thenReturn(BigDecimal.valueOf(0.05));
        when(domainMessage.getSegmentCount()).thenReturn(Optional.of("2"));
        when(domainMessage.getStatus()).thenReturn(MessageStatus.DELIVERED);
        when(domainMessage.getCurrency()).thenReturn(Currency.getInstance("USD"));

        // Map to Document
        MessageDocument document = messageMapper.toDocument(domainMessage);

        // Verify Document fields
        assertEquals(domainMessage.getId(), document.getResourceId());
        assertEquals(domainMessage.getSender().get(), document.getFrom());
        assertEquals(domainMessage.getRecipient(), document.getTo());
        assertEquals(domainMessage.getMessageContent(), document.getContent());
        assertEquals(Map.of("image", "https://example.com/image.jpg"), document.getMedia());
    }

    @Test
    void testMapOptional() {
        Optional<String> valuePresent = Optional.of("value");
        Optional<String> valueEmpty = Optional.empty();

        assertEquals("value", messageMapper.mapOptional(valuePresent));
        assertNull(messageMapper.mapOptional(valueEmpty));
    }

    @Test
    void testMapOptionalToInteger() {
        Optional<String> valuePresent = Optional.of("42");
        Optional<String> valueEmpty = Optional.empty();

        assertEquals(42, messageMapper.mapOptionalToInteger(valuePresent));
        assertNull(messageMapper.mapOptionalToInteger(valueEmpty));
    }

    @Test
    void testMapOptionalMap() {
        Map<String, String> mediaMap = new HashMap<>();
        mediaMap.put("image", "https://example.com/image.jpg");

        Optional<Map<String, String>> valuePresent = Optional.of(mediaMap);
        Optional<Map<String, String>> valueEmpty = Optional.empty();

        assertEquals(mediaMap, messageMapper.mapOptionalMap(valuePresent));
        assertEquals(new HashMap<>(), messageMapper.mapOptionalMap(valueEmpty));
    }

    @Test
    void testNullMedia() {
        assertEquals(new ArrayList<>(), messageMapper.mapMedia(null));
    }
}
