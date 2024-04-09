package com.crm.smsmanagementservice.dto.request.sms;

import com.crm.smsmanagementservice.util.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;

/**
 * This class represents a data transfer object for incoming bulk SMS send requests.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/23/2024, Friday
 */
public record SMSBulkSendRequestDto(
        @NotNull(message = "Sender cannot be null")
        @ValidPhoneNumber(message = "Sender is not a valid phone number")
        @JsonProperty("sender")
        String sender,

        @NotNull(message = "Message content cannot be null")
        @NotBlank(message = "Message content cannot be blank")
        @JsonProperty("content") String messageContent,

        @JsonProperty("recipients")
        @NotNull.List({@NotNull(message = "Recipient cannot be null")})
        List<@ValidPhoneNumber(message = "Recipient is not a valid phone number") String> recipients

) {}