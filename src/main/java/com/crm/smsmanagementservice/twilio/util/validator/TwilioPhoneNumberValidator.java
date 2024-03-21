package com.crm.smsmanagementservice.twilio.util.validator;


import com.crm.smsmanagementservice.util.validator.IPhoneNumberValidator;
import com.twilio.exception.ApiException;
import com.twilio.rest.lookups.v2.PhoneNumber;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */

@Component @Slf4j
public class TwilioPhoneNumberValidator implements IPhoneNumberValidator {
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
            // Free Phone Number Validation from Twilio
            PhoneNumber number = PhoneNumber.fetcher(value).fetch();
            return number.getValid();
        } catch (ApiException e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            // TODO: Throw a custom exception
            throw e;
        }
    }
}