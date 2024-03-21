package com.crm.smsmanagementservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/19/2024, Tuesday
 */
public interface IPhoneNumberValidator extends ConstraintValidator<ValidPhoneNumber, String> {
    boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext);
}
