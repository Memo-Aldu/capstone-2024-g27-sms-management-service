package com.crm.smsmanagementservice.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * This class represents a domain exception.
 * It contains all the fields required to represent an exception in the domain.
 * It extends RuntimeException.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@Getter @Setter
public class DomainException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final String message;

    /**
     * This constructor is used to create an instance of DomainException.
     * @param code the domain exception code
     * @param status the HTTP status
     * @param message the exception message
     */
    public DomainException(String code, HttpStatus status, String message) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
    }

    /**
     * This constructor is used to create an instance of DomainException.
     * @param error the domain error
     */
    public DomainException(Error error) {
        code = error.getCode();
        message = error.getReason();
        status = error.getStatus();
    }

    public DomainException(Error error, String message) {
        code = error.getCode();
        this.message = message;
        status = error.getStatus();
    }
}
