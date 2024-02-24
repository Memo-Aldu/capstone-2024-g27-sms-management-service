package com.crm.smsmanagementservice.util;


import com.twilio.exception.ApiException;
import com.twilio.rest.lookups.v2.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */

@Configuration @Slf4j
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
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
