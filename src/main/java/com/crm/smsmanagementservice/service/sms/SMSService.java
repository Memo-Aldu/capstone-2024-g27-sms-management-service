package com.crm.smsmanagementservice.service.sms;

import com.crm.smsmanagementservice.config.CallbackProperties;
import com.crm.smsmanagementservice.config.TwilioConfig;
import com.crm.smsmanagementservice.dto.request.*;
import com.crm.smsmanagementservice.dto.response.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.DtoDocumentMapper;
import com.crm.smsmanagementservice.mapper.MessageDocumentMapper;
import com.crm.smsmanagementservice.repository.SMSRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Service
@AllArgsConstructor
@Slf4j
public class SMSService implements ISMSService {
    private final SMSRepository smsRepository;
    @Qualifier("messageDocumentMapper")
    private final MessageDocumentMapper messageMapper;
    private final DtoDocumentMapper dtoDocumentMapper;
    private final TwilioConfig twilioConfig;
    private final CallbackProperties callbackProperties;
    private static final List<MessageStatus> NON_TERMINAL_STATUSES = List.of
            (MessageStatus.QUEUED, MessageStatus.SENT, MessageStatus.ACCEPTED, MessageStatus.SCHEDULED);
    @Override
    public SMSSendResponseDto sendSMS(SMSSendRequestDto request) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(request.recipient()),
                    new PhoneNumber(twilioConfig.getTrialNumber()),
                    request.messageContent()
            ).setStatusCallback(callbackProperties.getSmsStatusEndpoint()).create();
            log.info("SMS sent successfully: {}", message);
            SmSDocument document = smsRepository.save(messageMapper.toDocument(message));
            return dtoDocumentMapper.toSMSSendResponseDto(document);
        } catch (DataAccessException e) {
            log.error("Failed to save SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    @Override
    public SMSScheduleResponseDto scheduleSMS(SMSScheduleRequestDto request) {
        try {
            log.info("Service SID: {}", twilioConfig.getSchedulingServiceSid());
            Message message = Message.creator(
                    new PhoneNumber(request.recipient()),
                    twilioConfig.getSchedulingServiceSid(),
                    request.messageContent())
                    .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                    .setSendAt(request.scheduleTime())
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .setFrom(new PhoneNumber(twilioConfig.getTrialNumber()))
                    .create();

            log.info("SMS scheduled successfully: {}", message);

            SmSDocument document = smsRepository.save(messageMapper.toDocument(message));
            document.setScheduledTime(request.scheduleTime());
            return dtoDocumentMapper.toSMSScheduleResponseDto(document);
        } catch (DataAccessException e) {
            log.error("Failed to save scheduled SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Failed to schedule SMS: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    @Override
    public SMSBulkSendResponseDto sendBulkSMS(SMSBulkSendRequestDto request) {
        log.info("Sending bulk SMS: {}", request);
        try {
            List<SMSSendResponseDto> messages = new ArrayList<>();
            request.recipients().parallelStream().forEach(recipient -> {
                Message message = Message.creator(
                        new PhoneNumber(recipient),
                        twilioConfig.getBulkServiceSid(),
                        request.messageContent())
                        .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                        .setFrom(twilioConfig.getTrialNumber())
                        .create();
                log.info("SMS sent successfully: {}", message);
                 SmSDocument document = smsRepository.save(messageMapper.toDocument(message));
                 messages.add(dtoDocumentMapper.toSMSSendResponseDto(document));
            });
            return new SMSBulkSendResponseDto(messages);
        } catch (DataAccessException e) {
            log.error("Failed to save bulk SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Failed to send bulk SMS: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }

    }

    @Override
    public SMSBulkScheduleResponseDto scheduleBulkSMS(SMSBulkScheduleRequestDto request) {
        log.info("Scheduling bulk SMS: {}", request);
        try {
            List<SMSSendResponseDto> messages = new ArrayList<>();
            request.recipients().parallelStream().forEach(recipient -> {
                Message message = Message.creator(
                        new PhoneNumber(recipient),
                        twilioConfig.getBulkServiceSid(),
                        request.messageContent())
                        .setStatusCallback(callbackProperties.getSmsStatusEndpoint())
                        .setFrom(twilioConfig.getTrialNumber())
                        .setSendAt(request.scheduleTime())
                        .setScheduleType(Message.ScheduleType.FIXED)
                        .create();
                log.info("SMS scheduled successfully: {}", message);
                SmSDocument document = messageMapper.toDocument(message);
                document.setScheduledTime(request.scheduleTime());
                document = smsRepository.save(document);
                messages.add(dtoDocumentMapper.toSMSSendResponseDto(document));
            });
            return new SMSBulkScheduleResponseDto(messages, request.scheduleTime());
        } catch (DataAccessException e) {
            log.error("Failed to save scheduled bulk SMS: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Failed to schedule bulk SMS: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    @Override
    public void updateSMSStatus(IMessageStatusCallback smsStatus) {
        log.info("Updating SMS status: {}", smsStatus);
        try {
            SmSDocument document = smsRepository.findById(smsStatus.getMessageId())
                    .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
            MessageStatus status = MessageStatus.fromString(smsStatus.getStatus());
            if (status == MessageStatus.DELIVERED) {
                document.setDeliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
            } else if (status == MessageStatus.FAILED || status == MessageStatus.UNDELIVERED) {
                document.setErrorCode(smsStatus.getErrorCode().orElse(null));
                document.setErrorMessage(smsStatus.getErrorMessage().orElse(null));
            }
            document.setStatus(status);
            smsRepository.save(document);
        } catch (DataAccessException e) {
            log.error("Failed to update SMS status in DB: {}", e.getMessage());
            throw new DomainException(Error.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Failed to update SMS status: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    @Async
    @Scheduled(fixedDelay = 10000, timeUnit = TimeUnit.MILLISECONDS)
    @Override
    public void pollSMSStatus() {
        if (twilioConfig.isPollForStatus()) {
            log.info("Polling SMS status");
            List<SmSDocument> nonTerminalMessages = smsRepository.findAllByStatus(NON_TERMINAL_STATUSES);
            log.info("Found {} Non-terminal messages: {}", nonTerminalMessages.size(), nonTerminalMessages);
            for (SmSDocument document : nonTerminalMessages) {
                try {
                    Message message = Message.fetcher(document.getId()).fetch();
                    MessageStatus status = MessageStatus.fromString(message.getStatus().toString());
                    if (status == document.getStatus()) {
                        continue;
                    }
                    else if (status != MessageStatus.DELIVERED) {
                        document.setDeliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
                    }
                    else if (status == MessageStatus.FAILED || status == MessageStatus.UNDELIVERED) {
                        document.setErrorCode(message.getErrorCode().toString());
                        document.setErrorMessage(message.getErrorMessage());
                    }
                    document.setStatus(status);
                    smsRepository.save(document);
                } catch (Exception e) {
                    log.error("Failed to poll SMS status: {}", e.getMessage());
                }
            }
        }
    }
}