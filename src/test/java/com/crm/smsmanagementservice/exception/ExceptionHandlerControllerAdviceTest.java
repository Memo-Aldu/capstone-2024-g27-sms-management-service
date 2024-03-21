package com.crm.smsmanagementservice.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerControllerAdviceTest {

    private ExceptionHandlerControllerAdvice exceptionHandlerControllerAdvice;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandlerControllerAdvice = new ExceptionHandlerControllerAdvice();
        request = new MockHttpServletRequest();
    }

    @Test
    void handleValidationExceptions() {
        // Create a MethodArgumentNotValidException
        String fieldName = "testField";
        String errorMessage = "Bad request parameters provided, please check the request body";
        FieldError fieldError = new FieldError("testObject", fieldName, errorMessage);
        MethodParameter param = new MethodParameter(ExceptionHandlerControllerAdvice.class.getDeclaredMethods()[0], -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                param, new BeanPropertyBindingResult(null, "testObject")
        );
        ex.getBindingResult().addError(fieldError);

        // Act
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandlerControllerAdvice
                .handleValidationExceptions(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(errorMessage, responseEntity.getBody().getMessage());
    }

 @Test
    void handleDomainException() {
        // Create a DomainException
        String errorCode = "123";
        String message = "Domain Exception occurred";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        DomainException ex = new DomainException(errorCode, status, message);

        // Act
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandlerControllerAdvice.handleDomainException(ex, request);

        // Assert
        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(errorCode, responseEntity.getBody().getCode());
        assertEquals(message, responseEntity.getBody().getMessage());
    }

    @Test
    void handleRuntimeException() {
        // Create a RuntimeException
        String message = "An error occurred";
        RuntimeException ex = new RuntimeException(message);

        // Act
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandlerControllerAdvice.handleException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("000", responseEntity.getBody().getCode());
        assertEquals(message, responseEntity.getBody().getMessage());
    }
}