package com.crm.smsmanagementservice.message.persistence;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Map;

import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.message.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class represents a document of message entity.
 * It contains all the fields required to store a message in the database.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024; Sunday
 */
@Getter @Setter @Builder
@Document(collection = "messages")
public class MessageDocument {
    @Id
    private String id;
    private String resourceId;
    private String conversationId;
    private String to;
    private String from;
    private Integer messageSegmentCount;
    private BigDecimal price;
    private String contactId;
    private String userId;
    private String country;
    private String carrier;
    private MessageStatus status;
    private String content;
    private Map<String, String> media;
    private Currency currency;
    private MessageDirection direction;
    private MessageType type;
    private ZonedDateTime scheduledDate;
    private ZonedDateTime createdDate;
    private ZonedDateTime deliveredTime;
    private ZonedDateTime updatedDate;
    private String resourceUri;
    private String serviceSid;
    private String providerId;
    private String errorCode;
    private String errorMessage;
    private String apiVersion;

    public boolean canCancel() {
        return this.status == MessageStatus.SCHEDULED || this.status == MessageStatus.QUEUED;
    }

    public boolean isCancelled() {
        return this.status == MessageStatus.CANCELLED;
    }
}