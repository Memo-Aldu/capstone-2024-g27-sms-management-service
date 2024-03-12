package com.crm.smsmanagementservice.dto.request;

import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/29/2024, Thursday
 */
public interface IMessageStatusCallback {
    String getMessageId();
    String getStatus();
    Optional<String> getErrorCode();
    Optional<String> getErrorMessage();
    default Optional<String> getAccountId() {
        return Optional.empty();
    }
}