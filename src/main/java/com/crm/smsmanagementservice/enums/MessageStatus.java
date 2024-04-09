package com.crm.smsmanagementservice.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * This enum represents the status of a message.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@AllArgsConstructor @ToString
public enum MessageStatus {
    QUEUED("queued"),
    ACCEPTED("accepted"),
    SCHEDULED("scheduled"),
    CANCELLED("cancelled"),
    SENDING("sending"),
    SENT("sent"),
    DELIVERED("delivered"),
    FAILED("failed"),
    CANCELED("canceled"),
    UNDELIVERED("undelivered"),
    UNKNOWN("unknown");


    private final String status;

    /**
     * This method returns the MessageStatus enum from a string.
     * @param status status
     * @return MessageStatus enum
     */
    public static MessageStatus fromString(String status) {
        for (MessageStatus messageStatus : MessageStatus.values()) {
            if (messageStatus.status.equalsIgnoreCase(status)) {
                return messageStatus;
            }
        }
        return UNKNOWN;
    }

}
