package com.crm.smsmanagementservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * This enum represents the error in the domain.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
@AllArgsConstructor @Getter
public enum Error {
    UNEXPECTED_ERROR(
            "0000500",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Unexpected error, please contact administrator",
            "Server"),
    ENTITY_NOT_FOUND(
            "0000404",
            HttpStatus.NOT_FOUND,
            "Entity not found",
            "Server"),
    INVALID_REQUEST(
            "0000400",
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            "Server");

    private final String code;
    private final HttpStatus status;
    private final String reason;
    private final String source;
}
