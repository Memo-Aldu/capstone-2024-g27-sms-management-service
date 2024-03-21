package com.crm.smsmanagementservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
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
