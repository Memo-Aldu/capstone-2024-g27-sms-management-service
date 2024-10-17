package com.crm.smsmanagementservice.provider.service;

import com.crm.smsmanagementservice.provider.service.twilio.TwilioPhoneNumberValidator;
import com.twilio.exception.ApiException;
import com.twilio.rest.lookups.v2.PhoneNumber;
import com.twilio.rest.lookups.v2.PhoneNumberFetcher;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-10-17, Thursday
 */
public class TwilioPhoneNumberValidatorTest {
    private TwilioPhoneNumberValidator phoneNumberValidator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        phoneNumberValidator = new TwilioPhoneNumberValidator();
    }

    @Test
    public void testIsValid_ValidPhoneNumber() {
        try (MockedStatic<PhoneNumber> mockedPhoneNumber = mockStatic(PhoneNumber.class)) {
            // Mock PhoneNumber and its fetcher behavior
            PhoneNumberFetcher fetcher = mock(PhoneNumberFetcher.class);
            PhoneNumber phoneNumber = mock(PhoneNumber.class);

            // Simulate valid phone number response
            mockedPhoneNumber.when(() -> PhoneNumber.fetcher(anyString())).thenReturn(fetcher);
            when(fetcher.fetch()).thenReturn(phoneNumber);
            when(phoneNumber.getValid()).thenReturn(true);

            // Validate a valid phone number
            boolean result = phoneNumberValidator.isValid("+1234567890", constraintValidatorContext);
            assertTrue(result);
        }
    }

    @Test
    public void testIsValid_InvalidPhoneNumber_404Error() {
        try (MockedStatic<PhoneNumber> mockedPhoneNumber = mockStatic(PhoneNumber.class)) {
            // Mock PhoneNumber and its fetcher behavior
            PhoneNumberFetcher fetcher = mock(PhoneNumberFetcher.class);

            // Simulate a 404 error for an invalid phone number
            mockedPhoneNumber.when(() -> PhoneNumber.fetcher(anyString())).thenReturn(fetcher);
            when(fetcher.fetch()).thenThrow(new ApiException("Not Found", 404));

            // Validate an invalid phone number
            boolean result = phoneNumberValidator.isValid("+1234567890", constraintValidatorContext);
            assertFalse(result);
        }
    }

    @Test
    public void testIsValid_NullPhoneNumber() {
        // Validate a null phone number
        boolean result = phoneNumberValidator.isValid(null, constraintValidatorContext);
        assertFalse(result);
    }

    @Test
    public void testIsValid_BlankPhoneNumber() {
        // Validate a blank phone number
        boolean result = phoneNumberValidator.isValid("   ", constraintValidatorContext);
        assertFalse(result);
    }

    @Test
    public void testIsValid_ApiExceptionOtherThan404() {
        try (MockedStatic<PhoneNumber> mockedPhoneNumber = mockStatic(PhoneNumber.class)) {
            // Mock PhoneNumber and its fetcher behavior
            PhoneNumberFetcher fetcher = mock(PhoneNumberFetcher.class);

            // Simulate an unexpected API exception
            mockedPhoneNumber.when(() -> PhoneNumber.fetcher(anyString())).thenReturn(fetcher);
            when(fetcher.fetch()).thenThrow(new ApiException("Internal Server Error", 500));

            // Verify that the unexpected exception is propagated
            ApiException exception = assertThrows(ApiException.class, () ->
                    phoneNumberValidator.isValid("+1234567890", constraintValidatorContext)
            );
            assertEquals(500, exception.getStatusCode());
        }
    }
}
