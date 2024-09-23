package com.crm.smsmanagementservice.conversation;

import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@RequiredArgsConstructor
public enum ConversationStatus {
    OPEN("open"),
    CLOSED("closed");

    private final String status;

    @JsonCreator
    public static ConversationStatus fromValue(String value) {
        for (ConversationStatus status : values()) {
            if (status.status.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new DomainException(Error.INVALID_REQUEST, "Invalid conversation status: " + value);
    }

    @JsonValue
    public String getStatus() {
        return this.status;
    }
}
