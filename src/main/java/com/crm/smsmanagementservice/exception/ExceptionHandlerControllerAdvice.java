package com.crm.smsmanagementservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@ControllerAdvice @Slf4j @Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, final HttpServletRequest request) {
        log.info("Handling Validation Exception: {}", ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String message = "Bad request parameters provided, please check the request body";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse("000400" , message, errors, request.getRequestURI()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, final HttpServletRequest request) {
        log.warn("Advice DomainException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(
                new ErrorResponse(ex.getCode(), ex.getMessage(), new HashMap<>(), request.getRequestURI()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, final HttpServletRequest request) {
        log.warn("Advice Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("000" , ex.getMessage(), new HashMap<>() ,request.getRequestURI()));
    }
}
