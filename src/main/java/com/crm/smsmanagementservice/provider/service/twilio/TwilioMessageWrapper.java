package com.crm.smsmanagementservice.provider.service.twilio;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import com.twilio.rest.api.v2010.account.Message;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This class wraps the Twilio message object. It implements the IMessageWrapper interface. It
 * abstracts the Twilio message object and provides an interface to access its properties.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/17/2024, Sunday
 */
@RequiredArgsConstructor @ToString
public class TwilioMessageWrapper implements DomainMessage {
  private final Message message;

  /**
   * This method returns the message ID.
   *
   * @return String The message ID.
   */
  @Override
  public String getId() {
    return message.getSid();
  }

  /**
   * This method returns the sender of the message.
   *
   * @return Optional The sender of the message.
   */
  @Override
  public Optional<String> getSender() {
    if (message.getFrom() == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(message.getFrom().toString());
  }

  @Override
  public Optional<String> getApiVersion() {
    return Optional.ofNullable(message.getApiVersion());
  }

  /**
   * This method returns the contactId of the message.
   * @return String The contactId of the message.
   */
  @Override
  public String getRecipient() {
    return message.getTo();
  }


  /**
   * This method returns the content of the message.
    * @return String The content of the message.
   */
  @Override
  public String getMessageContent() {
    return message.getBody();
  }

  @Override
  public BigDecimal getPrice() {
    if (message.getPrice() == null) {
      return new BigDecimal(0);
    }
    return new BigDecimal(message.getPrice());
  }

  @Override
  public Currency getCurrency() {
    return message.getPriceUnit();
  }

  @Override
  public String getURI() {
    return message.getUri();
  }

  /**
   * This method returns the created time of the message.
   * @return ZonedDateTime The created time of the message.
   */
  @Override
  public ZonedDateTime getCreatedTime() {
    return message.getDateCreated();
  }

  /**
   * This method returns the delivered time of the message.
   * @return ZonedDateTime The delivered time of the message.
   */
  @Override
  public ZonedDateTime getDeliveredTime() {
    return message.getDateSent();
  }

  /**
   * This method returns the scheduled time of the message.
   * @return ZonedDateTime The scheduled time of the message.
   */
  @Override
  public ZonedDateTime getScheduledTime() {
    return message.getDateSent();
  }

  @Override
  public ZonedDateTime getDateUpdated() {
    return message.getDateUpdated();
  }

  /**
   * This method returns the status of the message.
   * @return MessageStatus The status of the message.
   */
  @Override
  public MessageStatus getStatus() {
    return MessageStatus.valueOf(message.getStatus().name());
  }

  @Override
  public MessageDirection getDirection() {
    return MessageDirection.valueOf(message.getDirection().name());
  }

  /**
   * This method returns the provider ID of the message.
   * @return String The provider ID of the message.
   */
  @Override
  public String getProviderId() {
    return message.getSid();
  }

  /**
   * This method returns the service SID of the message.
    * @return Optional The service SID of the message.
   */
  @Override
  public Optional<String> getServiceSid() {
    return Optional.ofNullable(message.getMessagingServiceSid());
  }

  /**
   * This method returns the error code of the message.
   * @return Optional The error code of the message.
   */
  @Override
  public Optional<String> getErrorCode() {
    if (message.getErrorCode() != null) {
      return Optional.of(message.getErrorCode().toString());
    } else {
      return Optional.empty();
    }
  }

  /**
   * This method returns the error message of the message.
    * @return Optional The error message of the message.
   */
  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(message.getErrorMessage());
  }

  /**
   * This method returns the segment count of the message.
    * @return Optional The segment count of the message.
   */
  @Override
  public Optional<String> getSegmentCount() {
    return Optional.ofNullable(message.getNumSegments());
  }

  /**
   * This method returns the media count of the message.
    * @return Optional The media count of the message.
   */
  @Override
  public Optional<String> getMediaCount() {
    return Optional.ofNullable(message.getNumMedia());
  }

  /**
   * This method returns the media URLs of the message.
    * @return Optional The media URLs of the message.
   */
  @Override
  public Optional<Map<String, String>> getMediaUrls() {
    return Optional.ofNullable(message.getSubresourceUris());
  }
}
