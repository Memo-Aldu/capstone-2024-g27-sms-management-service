package com.crm.smsmanagementservice.provider.service;


import com.crm.smsmanagementservice.core.dto.DomainMessage;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * This interface defines the structure of a messaging service.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/16/2024, Saturday
 */
public interface MessagingClient {
     DomainMessage sendSMSFromNumber(String to, String from, String body);
     DomainMessage sendMMSFromNumber(String to, String from, String body, List<String> mediaUrls);
     DomainMessage scheduleSMS(String to, String body, ZonedDateTime sendAfter);
     DomainMessage scheduleMMS(String to, String body, List<String> mediaUrls, ZonedDateTime sendAfter);
     DomainMessage sendSMSFromNumber(String to, String body);
     DomainMessage sendMMSFromNumber(String to, String body, List<String> mediaUrls);
     DomainMessage sendSMSFromService(String to, String body);
     DomainMessage sendMMSFromService(String to, String body, List<String> mediaUrls);
     DomainMessage fetchMessageById(String id);
     DomainMessage cancelMessage(String id);
}
