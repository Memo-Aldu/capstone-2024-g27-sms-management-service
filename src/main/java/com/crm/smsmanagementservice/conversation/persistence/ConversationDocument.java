package com.crm.smsmanagementservice.conversation.persistence;

import java.time.ZonedDateTime;

import com.crm.smsmanagementservice.conversation.ConversationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Getter
@Setter
@Builder @ToString
@Document(collection = "conversations")
public class ConversationDocument {
    @Id
    private String id;
    private String userId;
    private String contactId;
    private String conversationName;
    private ConversationStatus status;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;
}
