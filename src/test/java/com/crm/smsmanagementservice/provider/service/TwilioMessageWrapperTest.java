package com.crm.smsmanagementservice.provider.service;

import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.provider.service.twilio.TwilioMessageWrapper;
import com.twilio.rest.api.v2010.account.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TwilioMessageWrapperTest {

    @Mock
    private Message twilioMessage;

    private TwilioMessageWrapper messageWrapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageWrapper = new TwilioMessageWrapper(twilioMessage);
    }

    @Test
    void testGetId() {
        when(twilioMessage.getSid()).thenReturn("SM123456789");
        assertEquals("SM123456789", messageWrapper.getId());
    }

    @Test
    void testGetSender() {
        when(twilioMessage.getFrom()).thenReturn(new com.twilio.type.PhoneNumber("+1234567890"));
        assertEquals(Optional.of("+1234567890"), messageWrapper.getSender());
    }

    @Test
    void testGetRecipient() {
        when(twilioMessage.getTo()).thenReturn("+0987654321");
        assertEquals("+0987654321", messageWrapper.getRecipient());
    }

    @Test
    void testGetMessageContent() {
        when(twilioMessage.getBody()).thenReturn("Hello World");
        assertEquals("Hello World", messageWrapper.getMessageContent());
    }

    @Test
    void testGetPrice() {
        when(twilioMessage.getPrice()).thenReturn("0.01");
        assertEquals(new BigDecimal("0.01"), messageWrapper.getPrice());
    }

    @Test
    void testGetPrice_Null() {
        when(twilioMessage.getPrice()).thenReturn(null);
        assertEquals(BigDecimal.ZERO, messageWrapper.getPrice());
    }

    @Test
    void testGetCurrency() {
        Currency usd = Currency.getInstance("USD");
        when(twilioMessage.getPriceUnit()).thenReturn(usd);
        assertEquals(usd, messageWrapper.getCurrency());
    }

    @Test
    void testGetStatus() {
        when(twilioMessage.getStatus()).thenReturn(Message.Status.DELIVERED);
        assertEquals(MessageStatus.DELIVERED, messageWrapper.getStatus());
    }

    @Test
    void testGetDirection() {
        when(twilioMessage.getDirection()).thenReturn(Message.Direction.OUTBOUND_API);
        assertEquals(MessageDirection.OUTBOUND_API, messageWrapper.getDirection());
    }

    @Test
    void testGetErrorCode() {
        when(twilioMessage.getErrorCode()).thenReturn(12345);
        assertEquals(Optional.of("12345"), messageWrapper.getErrorCode());
    }

    @Test
    void testGetErrorMessage() {
        when(twilioMessage.getErrorMessage()).thenReturn("Error occurred");
        assertEquals(Optional.of("Error occurred"), messageWrapper.getErrorMessage());
    }

    @Test
    void testGetMediaUrls() {
        Map<String, String> mediaUrls = Map.of("image", "http://example.com/image.jpg");
        when(twilioMessage.getSubresourceUris()).thenReturn(mediaUrls);
        assertEquals(Optional.of(mediaUrls), messageWrapper.getMediaUrls());
    }

    @Test
    void testGetDateCreated() {
        ZonedDateTime now = ZonedDateTime.now();
        when(twilioMessage.getDateCreated()).thenReturn(now);
        assertEquals(now, messageWrapper.getCreatedTime());
    }

    @Test
    void testGetDateSent() {
        ZonedDateTime now = ZonedDateTime.now();
        when(twilioMessage.getDateSent()).thenReturn(now);
        assertEquals(now, messageWrapper.getDeliveredTime());
    }

    @Test
    void testGetDateUpdated() {
        ZonedDateTime now = ZonedDateTime.now();
        when(twilioMessage.getDateUpdated()).thenReturn(now);
        assertEquals(now, messageWrapper.getDateUpdated());
    }

    @Test
    void testGetServiceSid() {
        when(twilioMessage.getMessagingServiceSid()).thenReturn("MG12345");
        assertEquals(Optional.of("MG12345"), messageWrapper.getServiceSid());
    }

    @Test
    void testGetSegmentCount() {
        when(twilioMessage.getNumSegments()).thenReturn("1");
        assertEquals(Optional.of("1"), messageWrapper.getSegmentCount());
    }

    @Test
    void testGetMediaCount() {
        when(twilioMessage.getNumMedia()).thenReturn("2");
        assertEquals(Optional.of("2"), messageWrapper.getMediaCount());
    }
}
