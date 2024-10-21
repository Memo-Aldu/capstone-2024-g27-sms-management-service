package com.crm.smsmanagementservice.provider.service.twilio;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.provider.service.MessagingClient;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This class is a service that handles operations related to the Twilio API.
 * It has methods for sending and scheduling SMS and MMS messages, and for fetching the status of a message.
 * It uses the TwilioConfig to get the configuration for the Twilio API, and the CallbackProperties to get the configuration for the callback.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/16/2024, Saturday
 */
@Service @Slf4j @RequiredArgsConstructor
public class TwilioClient implements MessagingClient {
    private final TwilioConfig twilioConfig;
    /**
     * This method is used to send an SMS from a specific number.
     * @param to the contactId's phone number
     * @param from the sender's phone number
     * @param body the message body
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendSMSFromNumber(String to, String from, String body) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(to), new PhoneNumber(from), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS from number: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to send an MMS from a specific number.
     * @param to the contactId's phone number
     * @param from the sender's phone number
     * @param body the message body
     * @param mediaUrls a list of URLs of the media to be sent
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendMMSFromNumber(String to, String from, String body, List<String> mediaUrls) {
        try {
            List<URI> media = new ArrayList<>();
            if (!mediaUrls.isEmpty()) {
                media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            }
            Message message = Message.creator(
                    new PhoneNumber(to), new PhoneNumber(from), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send MMS from number: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to schedule an SMS.
     * @param to the contactId's phone number
     * @param body the message body
     * @param sendAfter the time at which the message should be sent
     * @return an IDomainMessage object containing the details of the scheduled message
     */
    @Override
    public DomainMessage scheduleSMS(String to, String body, ZonedDateTime sendAfter) {
        try {
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getSchedulingServiceSid(), body)
                    .setSendAt(sendAfter)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to schedule SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to schedule an MMS.
     * @param to the contactId's phone number
     * @param body the message body
     * @param mediaUrls a list of URLs of the media to be sent
     * @param sendAfter the time at which the message should be sent
     * @return an IDomainMessage object containing the details of the scheduled message
     */
    @Override
    public DomainMessage scheduleMMS(String to, String body, List<String> mediaUrls, ZonedDateTime sendAfter) {
        try {
            List<URI> media = new ArrayList<>();
            if (!mediaUrls.isEmpty()) {
                media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            }
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getSchedulingServiceSid(), body)
                    .setSendAt(sendAfter)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to schedule MMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to send an SMS from the twilio number.
     * @param to the contactId's phone number
     * @param body the message body
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendSMSFromNumber(String to, String body) {
        try {
            Message message = Message.creator(new PhoneNumber(to),
                            new PhoneNumber(twilioConfig.getTwilioNumber()), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .create();
            log.info("TWILIO API Sent SMS: {}", message.toString());
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to send SMS from number: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to send an MMS from the twilio number.
     * @param to the contactId's phone number
     * @param body the message body
     * @param mediaUrls a list of URLs of the media to be sent
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendMMSFromNumber(String to, String body, List<String> mediaUrls) {
        try {
            List<URI> media = new ArrayList<>();
            if (!mediaUrls.isEmpty()) {
                media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            }
            Message message = Message.creator(new PhoneNumber(to), new PhoneNumber(twilioConfig.getTwilioNumber()), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to send MMS from number: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to send an SMS from the bulk service.
     * @param to the contactId's phone number
     * @param body the message body
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendSMSFromService(String to, String body) {
        try {
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getBulkServiceSid(), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to send SMS from service: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to send an MMS from the bulk service.
     * @param to the contactId's phone number
     * @param body the message body
     * @param mediaUrls a list of URLs of the media to be sent
     * @return an IDomainMessage object containing the details of the sent message
     */
    @Override
    public DomainMessage sendMMSFromService(String to, String body, List<String> mediaUrls) {
        try {
            List<URI> media = new ArrayList<>();
            if (!mediaUrls.isEmpty()) {
                media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            }
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getBulkServiceSid(), body)
                    .setStatusCallback(twilioConfig.getStatusCallbackUrl())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to send MMS from service: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    /**
     * This method is used to fetch a message by its SID.
     * @param sid the SID of the message
     * @return an IDomainMessage object containing the details of the fetched message
     */
    @Override
    public DomainMessage fetchMessageById(String sid) {
        try {
            Message message = Message.fetcher(sid).fetch();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to fetch message: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public DomainMessage cancelMessage(String id) {
        try {
            Message message = Message.updater(id)
                    .setStatus(Message.UpdateStatus.CANCELED)
                    .update();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.warn("TWILIO API Failed to cancel message: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }
}
