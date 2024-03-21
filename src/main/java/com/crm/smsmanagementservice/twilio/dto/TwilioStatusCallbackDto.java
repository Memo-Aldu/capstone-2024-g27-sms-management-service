package com.crm.smsmanagementservice.twilio.dto;

import com.crm.smsmanagementservice.dto.request.IMessageStatusCallback;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/26/2024, Monday
 */
@Builder
public record TwilioStatusCallbackDto(
        @NotNull(message = "Account SID cannot be null")
        @NotBlank(message = "Account SID cannot be blank")
        @JsonProperty("AccountSid") String accountSid,
        @NotNull(message = "Message SID cannot be null")
        @NotBlank(message = "Message SID cannot be blank")
        @JsonProperty("MessageSid") String messageSid,
        @NotNull(message = "SMS SID cannot be null")
        @NotBlank(message = "SMS SID cannot be blank")
        @JsonProperty("SmsSid") String smsSid,
        @NotNull(message = "Message status cannot be null")
        @NotBlank(message = "Message status cannot be blank")
        @JsonProperty("MessageStatus") String smsStatus,
        @NotNull(message = "Error code cannot be null")
        @NotBlank(message = "Error code cannot be blank")
        @JsonProperty("ErrorCode") String errorCode,
        @NotNull(message = "Error message cannot be null")
        @NotBlank(message = "Error message cannot be blank")
        @JsonProperty("ErrorMessage") String errorMessage
) implements IMessageStatusCallback {
        @Override
        public String getMessageId() {
                return messageSid;
        }

        @Override
        public String getStatus() {
                return smsStatus;
        }

        @Override
        public Optional<String> getErrorCode() {
                return Optional.of(errorCode);
        }

        @Override
        public Optional<String> getErrorMessage() {
                return Optional.of(errorMessage);
        }

        @Override
        public Optional<String> getAccountId() {
                return Optional.of(accountSid);
        }
}