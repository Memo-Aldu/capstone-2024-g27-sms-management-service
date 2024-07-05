package com.crm.smsmanagementservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Getter
@Setter
@Builder
@Document(collection = "conversations")
public class ConversationDocument {
    @Id
    private String id;
    private String sender;
    private String recipient;
    private String conversationName;
    private ZonedDateTime createdTime;
    private ZonedDateTime updatedTime;
}
