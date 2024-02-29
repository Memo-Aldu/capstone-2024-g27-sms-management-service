package com.crm.smsmanagementservice.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
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
    UNDELIVERED("undelivered");


    private final String status;

}
