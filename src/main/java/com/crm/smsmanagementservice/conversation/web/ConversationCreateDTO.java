package com.crm.smsmanagementservice.conversation.web;

import com.crm.smsmanagementservice.core.validator.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
public record ConversationCreateDTO(
        @JsonProperty("userId")
        @NotNull(message = "User Id cannot be null")
        @NotBlank(message = "User Id cannot be blank")
        String userId,

        @JsonProperty("contactId")
        @NotNull(message = "contact Id cannot be null")
        @NotBlank(message = "contact Id cannot be blank")
        String contactId,

        @JsonProperty("userNumber")
        @ValidPhoneNumber(message = "user number is not valid")
        String userNumber,

        @JsonProperty("contactNumber")
        @ValidPhoneNumber(message = "contact number is not valid")
        String contactNumber,

        @JsonProperty("conversationName")
        String conversationName
) {}
