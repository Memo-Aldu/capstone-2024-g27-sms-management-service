package com.crm.smsmanagementservice.twilio;

import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.twilio.service.TwilioService;
import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/18/2024, Monday
 */
@SpringBootTest @Slf4j
public class TwilioServiceIntegrationTest {
  @Autowired
  private TwilioService twilioService;

  @Value("${twilio.accountSid}")
  private String accountSid;

  @Value("${twilio.authToken}")
  private String authToken;

  @BeforeEach
  void setUp() {
    // Initialize Twilio with the test credentials
    Twilio.init(accountSid, authToken);
  }

  @Test
  public void whenSendSMSFromProvidedNumber_thenSuccess() {
    // Given
    String body = "Hello World";
    String validTwilioMagicNumber = "+15005550006";
    IMessageWrapper result = twilioService.sendSMSFromNumber(
            validTwilioMagicNumber, validTwilioMagicNumber, body);
    // Assertions to validate the behavior of your method
    assertNotNull(result);
    Assertions.assertEquals(validTwilioMagicNumber, result.getRecipient());
    Assertions.assertEquals(result.getMessageContent(), body);
    Assertions.assertNotNull(result.getId());
  }

  @Test
  public void whenSendSMSFromProvidedNumber_thenTrowException() {
    // Given
    String body = "Hello World";
    String invalidTwilioMagicNumber = "+15005550001";
    DomainException exception = Assertions.assertThrows(DomainException.class, () -> {
      twilioService.sendSMSFromNumber(
              "invalidTwilioMagicNumber", "invalidTwilioMagicNumber", body);
    });
    assertEquals(exception.getCode(), Error.INVALID_REQUEST.getCode());
    assertEquals(exception.getStatus(), Error.INVALID_REQUEST.getStatus());
  }
}
