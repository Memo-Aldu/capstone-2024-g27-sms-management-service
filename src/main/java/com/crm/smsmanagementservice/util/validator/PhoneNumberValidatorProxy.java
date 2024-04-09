package com.crm.smsmanagementservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This class is the proxy for the phone number validator.
 * It implements the ConstraintValidator interface.
 * It is used to validate phone numbers.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/19/2024, Tuesday
 */
@Component @RequiredArgsConstructor
public final class PhoneNumberValidatorProxy implements ConstraintValidator<ValidPhoneNumber, String> {
    private final IPhoneNumberValidator delegateValidator;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return delegateValidator.isValid(value, context);
    }
}
