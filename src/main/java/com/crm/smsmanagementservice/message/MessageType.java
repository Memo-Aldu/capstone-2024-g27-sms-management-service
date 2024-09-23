package com.crm.smsmanagementservice.message;

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
public enum MessageType {
    MMS("mms"),
    SMS("sms");

    private final String type;
}