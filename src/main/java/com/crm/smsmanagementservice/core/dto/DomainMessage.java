package com.crm.smsmanagementservice.core.dto;

import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

/**
 * This interface defines the structure of a message.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/17/2024, Sunday
 */
public interface DomainMessage {
  String getId();
  String getRecipient();
  String getMessageContent();
  BigDecimal getPrice();
  Currency getCurrency();
  String getURI();
  ZonedDateTime getCreatedTime();
  ZonedDateTime getDeliveredTime();
  ZonedDateTime getScheduledTime();
  ZonedDateTime getDateUpdated();
  MessageStatus getStatus();
  MessageDirection getDirection();
  String getProviderId();
  Optional<String>  getSender();
  Optional<String> getApiVersion();
  Optional<String> getServiceSid();
  Optional<String> getErrorCode();
  Optional<String> getErrorMessage();
  Optional<String> getSegmentCount();
  Optional<String> getMediaCount();
  Optional<Map<String, String>> getMediaUrls();
}
