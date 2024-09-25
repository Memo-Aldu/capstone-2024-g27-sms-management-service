package com.crm.smsmanagementservice.conversation.web;

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

        @JsonProperty("conversationName")
        String conversationName
) {}
