package com.crm.smsmanagementservice.core.exception;

import com.crm.smsmanagementservice.core.dto.DomainAPIResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class is a controller advice that handles exceptions globally across the whole application.
 * It has three exception handlers: one for validation exceptions, one for domain exceptions, and one for runtime exceptions.
 * Each exception handler logs the exception, creates an error response, and returns it with the appropriate HTTP status.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@ControllerAdvice @Slf4j @Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerControllerAdvice {

    /**
     * This method is an exception handler for validation exceptions.
     * It logs the exception, creates a map of field errors, and returns an error response with a 400 Bad Request status.
     * @param ex the validation exception
     * @param request the HTTP request
     * @return ResponseEntity<ErrorResponse> the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DomainAPIResponse<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex, final HttpServletRequest request) {
        log.info("Handling Validation Exception: {}", ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String message = "Bad request parameters provided, please check the request body";
        DomainAPIResponse<ErrorResponse> response = DomainAPIResponse.<ErrorResponse>builder()
                .status(HttpStatus.BAD_REQUEST)
                .responseStatus(DomainAPIResponse.DomainAPIResponseStatus.ERROR)
                .message(message)
                .error(new ErrorResponse("000400" , message, errors, request.getRequestURI()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * This method is an exception handler for domain exceptions.
     * It logs the exception and returns an error response with the status of the exception.
     * @param ex the validation exception
     * @param request the HTTP request
     * @return ResponseEntity<ErrorResponse> the error response
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<DomainAPIResponse<ErrorResponse>> handleDomainException(DomainException ex, final HttpServletRequest request) {
        log.warn("Advice DomainException: {}", ex.getMessage());
        DomainAPIResponse<ErrorResponse> response = DomainAPIResponse.<ErrorResponse>builder()
                .status(ex.getStatus())
                .responseStatus(DomainAPIResponse.DomainAPIResponseStatus.ERROR)
                .message(ex.getMessage())
                .error(new ErrorResponse(ex.getCode(), ex.getMessage(), new HashMap<>(), request.getRequestURI()))
                .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * This method is an exception handler for runtime exceptions.
     * It logs the exception and returns an error response with a 500 Internal Server Error status.
     * @param ex the validation exception
     * @param request the HTTP request
     * @return ResponseEntity<ErrorResponse> the error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DomainAPIResponse<ErrorResponse>> handleException(Exception ex, final HttpServletRequest request) {
        log.warn("Advice Exception: {}", ex.getMessage());
        DomainAPIResponse<ErrorResponse> response = DomainAPIResponse.<ErrorResponse>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .responseStatus(DomainAPIResponse.DomainAPIResponseStatus.ERROR)
                .message(ex.getMessage())
                .error(new ErrorResponse("000500" , ex.getMessage(), new HashMap<>(), request.getRequestURI()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
