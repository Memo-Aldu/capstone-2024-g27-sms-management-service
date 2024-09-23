package com.crm.smsmanagementservice.provider.service.twilio;


import com.crm.smsmanagementservice.core.validator.IPhoneNumberValidator;
import com.twilio.exception.ApiException;
import com.twilio.rest.lookups.v2.PhoneNumber;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class validates the phone number using Twilio API.
 * It implements the IPhoneNumberValidator interface.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */

@Component @Slf4j
public class TwilioPhoneNumberValidator implements IPhoneNumberValidator {

    /**
     * This method validates the phone number using Twilio API.
     *
     * @param value The phone number to validate.
     * @param constraintValidatorContext The constraint validator context.
     * @return boolean True if the phone number is valid, false otherwise.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }

        value = value.replaceAll("[\\s()-]", "");

        if(value.isBlank()) {
            return false;
        }

        try {
            // Use Twilio API to validate the phone number
            log.info("Validating phone number: {}", value);
            PhoneNumber number = PhoneNumber.fetcher(value).fetch();
            log.info("Phone number {} is valid: {}", value, number.getValid());
            return number.getValid();
        } catch (ApiException e) {
            // If the phone number is invalid, Twilio API will throw a 404 error
            if (e.getStatusCode() == 404) {
                return false;
            }
            log.warn("Error occurred while validating phone number: {}", e.getMessage());
            throw e;
        }
    }
}