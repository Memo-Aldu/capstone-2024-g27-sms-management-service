package com.crm.smsmanagementservice.service.message;


import java.time.ZonedDateTime;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/16/2024, Saturday
 */
public interface IMessagingService {
     IMessageWrapper sendSMSFromNumber(String to, String from, String body);
     IMessageWrapper scheduleSMS(String to, String body, ZonedDateTime sendAfter);
     IMessageWrapper sendSMSFromNumber(String to, String body);
     IMessageWrapper sendSMSFromService(String to, String body);
     IMessageWrapper fetchMessageById(String id);
     boolean pollMessageStatus();
}
