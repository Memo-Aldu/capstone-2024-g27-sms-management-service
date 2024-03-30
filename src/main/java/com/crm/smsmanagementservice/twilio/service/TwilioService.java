package com.crm.smsmanagementservice.twilio.service;

import com.crm.smsmanagementservice.config.CallbackProperties;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.service.message.IMessagingService;
import com.crm.smsmanagementservice.twilio.config.TwilioConfig;
import com.crm.smsmanagementservice.twilio.wrapper.TwilioMessageWrapper;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/16/2024, Saturday
 */
@Service @Slf4j @RequiredArgsConstructor
public class TwilioService implements IMessagingService {
    private final TwilioConfig twilioConfig;
    private final CallbackProperties callbackProperties;

    @Override
    public IMessageWrapper sendSMSFromNumber(String to, String from, String body) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(to), new PhoneNumber(from), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper sendMMSFromNumber(String to, String from, String body, List<String> mediaUrls) {
        try {
            List<URI> media = new ArrayList<>();
            if (!mediaUrls.isEmpty()) {
                media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            }
            Message message = Message.creator(
                    new PhoneNumber(to), new PhoneNumber(from), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper scheduleSMS(String to, String body, ZonedDateTime sendAfter) {
        try {
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getSchedulingServiceSid(), body)
                    .setSendAt(sendAfter)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper scheduleMMS(String to, String body, List<String> mediaUrls, ZonedDateTime sendAfter) {
        try {
            List<URI> media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getSchedulingServiceSid(), body)
                    .setSendAt(sendAfter)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper sendSMSFromNumber(String to, String body) {
        try {
            Message message = Message.creator(new PhoneNumber(to),
                            new PhoneNumber(twilioConfig.getTrialNumber()), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .create();
            log.info("TWILIO API Sent SMS: {}", message.toString());
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper sendMMSFromNumber(String to, String body, List<String> mediaUrls) {
        try {
            List<URI> media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            Message message = Message.creator(new PhoneNumber(to), new PhoneNumber(twilioConfig.getTrialNumber()), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper sendSMSFromService(String to, String body) {
        try {
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getBulkServiceSid(), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper sendMMSFromService(String to, String body, List<String> mediaUrls) {
        try {
            List<URI> media = mediaUrls.stream().map(URI::create).collect(Collectors.toList());
            Message message = Message.creator(new PhoneNumber(to), twilioConfig.getBulkServiceSid(), body)
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setMediaUrl(media)
                    .create();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public IMessageWrapper fetchMessageById(String sid) {
        try {
            Message message = Message.fetcher(sid).fetch();
            return new TwilioMessageWrapper(message);
        } catch (ApiException e) {
            log.info("TWILIO API Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST.getCode(), Error.INVALID_REQUEST.getStatus(), e.getMessage());
        }
    }

    @Override
    public boolean pollMessageStatus() {
        return !callbackProperties.getIsHealthy() && twilioConfig.isPollForStatus();
    }
}
