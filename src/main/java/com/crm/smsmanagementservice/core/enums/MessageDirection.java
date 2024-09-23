package com.crm.smsmanagementservice.core.enums;

import lombok.RequiredArgsConstructor;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@RequiredArgsConstructor
public enum MessageDirection {
    INBOUND("inbound"),
    OUTBOUND_API("outbound-api"),
    UNKNOWN("unknown");
    private final String direction;
}