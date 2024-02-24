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
    SENT("sent"),
    DELIVERED("delivered"),
    FAILED("failed"),
    UNDELIVERED("undelivered"),
    RECEIVED("received");

    private final String status;

}
