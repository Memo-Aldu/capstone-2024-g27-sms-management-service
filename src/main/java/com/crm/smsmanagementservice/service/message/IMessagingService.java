package com.crm.smsmanagementservice.service.message;


import java.time.ZonedDateTime;
import java.util.List;


/**
 * This interface defines the structure of a messaging service.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/16/2024, Saturday
 */
public interface IMessagingService {
     IMessageWrapper sendSMSFromNumber(String to, String from, String body);
     IMessageWrapper sendMMSFromNumber(String to, String from, String body, List<String> mediaUrls);
     IMessageWrapper scheduleSMS(String to, String body, ZonedDateTime sendAfter);
     IMessageWrapper scheduleMMS(String to, String body, List<String> mediaUrls, ZonedDateTime sendAfter);
     IMessageWrapper sendSMSFromNumber(String to, String body);
     IMessageWrapper sendMMSFromNumber(String to, String body, List<String> mediaUrls);
     IMessageWrapper sendSMSFromService(String to, String body);
     IMessageWrapper sendMMSFromService(String to, String body, List<String> mediaUrls);
     IMessageWrapper fetchMessageById(String id);
     boolean pollMessageStatus();
}
