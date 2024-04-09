package com.crm.smsmanagementservice.service.message;

import com.crm.smsmanagementservice.enums.MessageStatus;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * This interface defines the structure of a message.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/17/2024, Sunday
 */
public interface IMessageWrapper {
  String getId();
  String getRecipient();
  String getMessageContent();
  ZonedDateTime getCreatedTime();
  ZonedDateTime getDeliveredTime();
  ZonedDateTime getScheduledTime();
  MessageStatus getStatus();
  String getProviderId();
  Optional<String> getSender();
  Optional<String> getServiceSid();
  Optional<String> getErrorCode();
  Optional<String> getErrorMessage();
  Optional<String> getSegmentCount();
  Optional<String> getMediaCount();
  Optional<Map<String, String>> getMediaUrls();
}
