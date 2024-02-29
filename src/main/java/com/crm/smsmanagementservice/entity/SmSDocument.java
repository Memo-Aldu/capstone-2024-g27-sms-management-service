package com.crm.smsmanagementservice.entity;


import com.crm.smsmanagementservice.enums.MessageStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Getter @Setter @Builder
@Document(collection = "sms_messages")
public class SmSDocument {

    @Id
    private String id;
    private String sender;
    private String recipient;
    private String messageContent;
    private ZonedDateTime createdTime;
    private ZonedDateTime deliveredTime;
    private ZonedDateTime scheduledTime;
    private MessageStatus status;
    private String providerId;
    private String errorCode;
    private String errorMessage;
    private Integer segmentCount;
    private Integer mediaCount;
    private List<String> mediaUrls;
}
