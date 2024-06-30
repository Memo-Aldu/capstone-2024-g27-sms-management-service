package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.service.message.IMessageStatusListenerService;
import com.crm.smsmanagementservice.twilio.dto.TwilioStatusCallbackDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/14/2024, Thursday
 */
public class CallbackControllerTest {

    @InjectMocks
    private CallbackController callbackController;

    @Mock
    private IMessageStatusListenerService statusListenerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldUpdateSMSStatusSuccessfully() {
        TwilioStatusCallbackDto callbackDto = TwilioStatusCallbackDto.builder()
                .accountSid("accountSid")
                .messageSid("messageSid")
                .smsSid("smsSid")
                .errorCode("errorCode")
                .errorMessage("errorMessage")
                .smsStatus(MessageStatus.DELIVERED.toString())
                .build();

    callbackController.smsStatusCallback(
        "messageStatus", "apiVersion", "smsSid",
        MessageStatus.DELIVERED.toString(), "to", "from", "messageSid",
        "accountSid", "errorCode", "errorMessage");

        verify(statusListenerService, times(1)).onMessageStatusChanged(callbackDto);
    }

    @Test
    public void shouldReturnOkOnHealthCheck() {
        ResponseEntity<?> result = callbackController.healthCheck();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
