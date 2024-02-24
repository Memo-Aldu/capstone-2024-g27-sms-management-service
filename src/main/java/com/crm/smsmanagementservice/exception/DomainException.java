package com.crm.smsmanagementservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@Getter @Setter
public class DomainException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final String message;

    public DomainException(String code, HttpStatus status, String message) {
        super();
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public DomainException(Error errorCode) {
        code = errorCode.getCode();
        message = errorCode.getReason();
        status = errorCode.getStatus();
    }
}
