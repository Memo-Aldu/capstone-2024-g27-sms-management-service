package com.crm.smsmanagementservice.provider.service;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.provider.service.twilio.TwilioClient;
import com.crm.smsmanagementservice.provider.service.twilio.TwilioConfig;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageFetcher;
import com.twilio.rest.api.v2010.account.MessageUpdater;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-10-08, Tuesday
 */

public class TwilioClientTest {
    @Mock
    private TwilioConfig twilioConfig;

    @InjectMocks
    private TwilioClient twilioClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        com.twilio.Twilio.init("testAccountSid", "testAuthToken");
    }

    @Test
    public void testSendSMSFromNumber_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("SM123456789");

            DomainMessage result = twilioClient.sendSMSFromNumber("+1234567890", "+0987654321", "Hello!");
            assertNotNull(result);
            assertEquals("SM123456789", result.getId());
        }
    }

    @Test
    public void testSendSMSFromNumber_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            // Mock the behavior to throw an ApiException
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            // Verify that the DomainException is thrown
            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendSMSFromNumber("+1234567890", "+0987654321", "Hello!")
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testSendMMSFromNumber_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        List<URI> mediaUrls = List.of(URI.create("http://example.com/media1"),
                URI.create("http://example.com/media2"));

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("MM123456789");

            DomainMessage result = twilioClient.sendMMSFromNumber("+1234567890", "+0987654321", "Hello!", mediaUrls.stream().map(URI::toString).toList());
            assertNotNull(result);
            assertEquals("MM123456789", result.getId());
        }
    }

    @Test
    public void testSendMMSFromNumber_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendMMSFromNumber("+1234567890", "+0987654321", "Hello!", List.of("http://media.com/image.jpg"))
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testSendSMSFromService_Success() {
        String callbackUrl = "http://example.com/status";
        String serviceSid = "SERVICE_SID";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getBulkServiceSid()).thenReturn(serviceSid);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), anyString(), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("SM123456789");

            DomainMessage result = twilioClient.sendSMSFromService("8198889999", "Hello Test");
            assertNotNull(result);
            assertEquals("SM123456789", result.getId());
        }
    }

    @Test
    public void testSendSMSFromService_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getBulkServiceSid()).thenReturn("BULK_SERVICE_SID");

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            // Mock the MessageCreator and simulate API exception
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), eq("BULK_SERVICE_SID"), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            // Test and verify that the exception is handled correctly
            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendSMSFromService("+1234567890", "Hello!")
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testSendMMSFromService_Success() {
        String callbackUrl = "http://example.com/status";
        String serviceSid = "SERVICE_SID";
        List<String> mediaUrls = List.of("http://example.com/media1",
                "http://example.com/media2");
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getBulkServiceSid()).thenReturn(serviceSid);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), anyString(), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("SM123456789");

            DomainMessage result = twilioClient.sendMMSFromService("8198889999", "Hello Test", mediaUrls);
            assertNotNull(result);
            assertEquals("SM123456789", result.getId());
        }
    }

    @Test
    public void testSendMMSFromService_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getBulkServiceSid()).thenReturn("BULK_SERVICE_SID");

        List<URI> mediaUrls = List.of(URI.create("http://example.com/media1"), URI.create("http://example.com/media2"));

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            // Mock the MessageCreator and simulate API exception
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), eq("BULK_SERVICE_SID"), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(mediaUrls)).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            // Test and verify that the exception is handled correctly
            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendMMSFromService("+1234567890", "Hello!", mediaUrls.stream().map(URI::toString).toList())
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testSendSMSFromDefaultNumber_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("SM123456789");

            DomainMessage result = twilioClient.sendSMSFromNumber("+1234567890", "Hello!");
            assertNotNull(result);
            assertEquals("SM123456789", result.getId());
        }
    }

    @Test
    public void testSendSMSFromDefaultNumber_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            // Mock the behavior to throw an ApiException
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            // Verify that the DomainException is thrown
            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendSMSFromNumber("+1234567890", "Hello!")
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testSendMMSFromDefaultNumber_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        List<URI> mediaUrls = List.of(URI.create("http://example.com/media1"),
                URI.create("http://example.com/media2"));

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("MM123456789");

            DomainMessage result = twilioClient.sendMMSFromNumber("+1234567890", "Hello!", mediaUrls.stream().map(URI::toString).toList());
            assertNotNull(result);
            assertEquals("MM123456789", result.getId());
        }
    }

    @Test
    public void testSendMMSFromDefaultNumber_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.sendMMSFromNumber("+1234567890", "Hello!", List.of("http://media.com/image.jpg"))
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testScheduleSMS_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getSchedulingServiceSid()).thenReturn("SCHEDULING_SERVICE_SID");

        ZonedDateTime sendAfter = ZonedDateTime.now().plusDays(1);

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), anyString(), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setSendAt(any())).thenReturn(messageCreator);
            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.setScheduleType(any(Message.ScheduleType.class))).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("SMSCHED123456789");

            DomainMessage result = twilioClient.scheduleSMS("+1234567890", "Hello!", sendAfter);
            assertNotNull(result);
            assertEquals("SMSCHED123456789", result.getId());
        }
    }

    @Test
    public void testScheduleSMS_Exception() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getSchedulingServiceSid()).thenReturn("SCHEDULING_SID");

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), eq("SCHEDULING_SID"), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setSendAt(any())).thenReturn(messageCreator);
            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.setScheduleType(Message.ScheduleType.FIXED)).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.scheduleSMS("+1234567890", "Scheduled Message", ZonedDateTime.now().plusHours(1))
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testScheduleMMS_Success() {
        String callbackUrl = "http://example.com/status";
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getSchedulingServiceSid()).thenReturn("SCHEDULING_SERVICE_SID");

        ZonedDateTime sendAfter = ZonedDateTime.now().plusDays(1);
        List<URI> mediaUrls = List.of(URI.create("http://example.com/media1"), URI.create("http://example.com/media2"));

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            Message message = mock(Message.class);

            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class), anyString(), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setSendAt(any())).thenReturn(messageCreator);
            when(messageCreator.setStatusCallback(anyString())).thenReturn(messageCreator);
            when(messageCreator.setScheduleType(any(Message.ScheduleType.class))).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(message);
            when(message.getSid()).thenReturn("MMSCHED123456789");

            DomainMessage result = twilioClient.scheduleMMS("+1234567890", "Hello!", mediaUrls.stream().map(URI::toString).toList(), sendAfter);
            assertNotNull(result);
            assertEquals("MMSCHED123456789", result.getId());
        }
    }

    @Test
    public void testScheduleMMS_Exception() {
        String callbackUrl = "http://example.com/status";
        List<String> mediaUrl = List.of("http://example.com/media1", "http://example.com/media2");
        when(twilioConfig.getStatusCallbackUrl()).thenReturn(callbackUrl);
        when(twilioConfig.getSchedulingServiceSid()).thenReturn("SCHEDULING_SERVICE_SID");

        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreator = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), eq("SCHEDULING_SERVICE_SID"), anyString()))
                    .thenReturn(messageCreator);

            when(messageCreator.setSendAt(any())).thenReturn(messageCreator);
            when(messageCreator.setStatusCallback(callbackUrl)).thenReturn(messageCreator);
            when(messageCreator.setScheduleType(Message.ScheduleType.FIXED)).thenReturn(messageCreator);
            when(messageCreator.setMediaUrl(anyList())).thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.scheduleMMS("+1234567890", "Scheduled Message", mediaUrl, ZonedDateTime.now().plusHours(1))
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testFetchMessageById_Success() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            // Mock MessageFetcher
            MessageFetcher messageFetcher = mock(MessageFetcher.class);
            Message message = mock(Message.class);

            // Mock the behavior of Message.fetcher(sid) to return the MessageFetcher mock
            mockedMessage.when(() -> Message.fetcher(anyString()))
                    .thenReturn(messageFetcher);

            // Mock the fetch() method of MessageFetcher to return a Message object
            when(messageFetcher.fetch()).thenReturn(message);
            when(message.getSid()).thenReturn("SM123456789");

            // Call the method under test
            DomainMessage result = twilioClient.fetchMessageById("SM123456789");

            // Verify that the method returns the expected DomainMessage
            assertNotNull(result);
            assertEquals("SM123456789", result.getId());
        }
    }

    @Test
    public void testFetchMessageById_Exception() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageFetcher messageFetcher = mock(MessageFetcher.class);
            mockedMessage.when(() -> Message.fetcher(anyString())).thenReturn(messageFetcher);

            when(messageFetcher.fetch()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.fetchMessageById("SM123456789")
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }

    @Test
    public void testCancelMessage_Success() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            Message message = mock(Message.class);
            MessageUpdater messageUpdater = mock(MessageUpdater.class);
            String sid = "SM123456789";

            mockedMessage.when(() -> Message.updater(sid).setStatus(Message.UpdateStatus.CANCELED).update())
                    .thenReturn(messageUpdater);

            when(messageUpdater.setStatus(Message.UpdateStatus.CANCELED)).thenReturn(messageUpdater);
            when(messageUpdater.update()).thenReturn(message);

            when(message.getSid()).thenReturn(sid);

            DomainMessage result = twilioClient.cancelMessage(sid);
            assertNotNull(result);
            assertEquals(sid, result.getId());
        }
    }

    @Test
    public void testCancelMessage_Exception() {
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageUpdater messageUpdater = mock(MessageUpdater.class);
            mockedMessage.when(() -> Message.updater(anyString())).thenReturn(messageUpdater);

            when(messageUpdater.setStatus(Message.UpdateStatus.CANCELED)).thenReturn(messageUpdater);
            when(messageUpdater.update()).thenThrow(new ApiException("Twilio API Error", 400));

            DomainException exception = assertThrows(DomainException.class, () ->
                    twilioClient.cancelMessage("SM123456789")
            );

            assertEquals(Error.INVALID_REQUEST.getCode(), exception.getCode());
            assertEquals(Error.INVALID_REQUEST.getStatus(), exception.getStatus());
            assertEquals("Twilio API Error", exception.getMessage());
        }
    }
}

