package com.crm.smsmanagementservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/15/2024, Friday
**/

public class DomainExceptionTest {

    @Test
    public void shouldCreateDomainExceptionWithErrorCode() {
        DomainException exception = new DomainException(Error.UNEXPECTED_ERROR);

        assertEquals(Error.UNEXPECTED_ERROR.getCode(), exception.getCode());
        assertEquals(Error.UNEXPECTED_ERROR.getReason(), exception.getMessage());
        assertEquals(Error.UNEXPECTED_ERROR.getStatus(), exception.getStatus());
    }

    @Test
    public void shouldCreateDomainExceptionWithCustomValues() {
        String code = "CUSTOM_CODE";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Custom message";

        DomainException exception = new DomainException(code, status, message);

        assertEquals(code, exception.getCode());
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    public void shouldThrowDomainExceptionWithErrorCode() {
        assertThrows(DomainException.class, () -> {
            throw new DomainException(Error.UNEXPECTED_ERROR);
        });
    }

    @Test
    public void shouldThrowDomainExceptionWithCustomValues() {
        String code = "CUSTOM_CODE";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Custom message";

        assertThrows(DomainException.class, () -> {
            throw new DomainException(code, status, message);
        });
    }
}
