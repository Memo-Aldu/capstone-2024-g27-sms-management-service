package com.crm.smsmanagementservice.conversation.web;

import com.crm.smsmanagementservice.conversation.ConversationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

/**
  * @author : memo-aldu
  * @mailto : maldu064@uOttawa.ca
  * @created : 2024-07-05, Friday
**/
public record ConversationUpdateDTO(
        @Nullable
        @JsonProperty("conversationName")
        String conversationName,
        @Nullable
        @JsonProperty("status")
        ConversationStatus status
) {
}
