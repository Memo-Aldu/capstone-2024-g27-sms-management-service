package com.crm.smsmanagementservice.twilio.wrapper;

import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/17/2024, Sunday
 */
@RequiredArgsConstructor
public class TwilioMessageWrapper implements IMessageWrapper {

  private final Message message;

  @Override
  public String getId() {
    return message.getSid();
  }

  @Override
  public Optional<String> getSender() {
    if (message.getFrom() != null) {
      return Optional.of(message.getFrom().toString());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public String getRecipient() {
    return message.getTo();
  }

  @Override
  public String getMessageContent() {
    return message.getBody();
  }

  @Override
  public ZonedDateTime getCreatedTime() {
    return message.getDateCreated();
  }

  @Override
  public ZonedDateTime getDeliveredTime() {
    return message.getDateSent();
  }

  @Override
  public ZonedDateTime getScheduledTime() {
    return message.getDateSent();
  }

  @Override
  public MessageStatus getStatus() {
    return MessageStatus.valueOf(message.getStatus().name());
  }

  @Override
  public String getProviderId() {
    return message.getSid();
  }

  @Override
  public Optional<String> getServiceSid() {
    return Optional.ofNullable(message.getMessagingServiceSid());
  }

  @Override
  public Optional<String> getErrorCode() {
    if(message.getErrorCode() != null) {
      return Optional.of(message.getErrorCode().toString());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(message.getErrorMessage());
  }

  @Override
  public Optional<String> getSegmentCount() {
    return Optional.ofNullable(message.getNumSegments());
  }

  @Override
  public Optional<String> getMediaCount() {
    return Optional.ofNullable(message.getNumMedia());
  }

  @Override
  public Optional<Map<String, String>> getMediaUrls() {
    return Optional.ofNullable(message.getSubresourceUris());
  }
}
