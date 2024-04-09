package com.crm.smsmanagementservice.service.sms;

import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.dto.request.*;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.DtoMapper;
import com.crm.smsmanagementservice.mapper.DocumentMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.message.IMessagingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * This class is a service that handles operations related to the SMS service.
 * It has four methods: one for sending an SMS, one for scheduling an SMS, one for sending bulk SMS, and one for scheduling bulk SMS.
 * It uses the IMessagingService to perform these operations and the MessageRepository to save the messages.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Service
@AllArgsConstructor
@Slf4j(topic = "SMSService")
public class SMSService implements ISMSService {
    private final MessageRepository smsRepository;
    private final DocumentMapper messageMapper;
    private final DtoMapper dtoDocumentMapper;
    private final IMessagingService messageService;
    private static final List<MessageStatus> NON_TERMINAL_STATUSES = List.of
            (MessageStatus.QUEUED, MessageStatus.SENT, MessageStatus.ACCEPTED, MessageStatus.SCHEDULED);

    /**
     * This method is used to send an SMS.
     * @param request the request object containing the details of the SMS to be sent
     * @return SMSSendResponseDto object containing the ID and status of the sent message
     */
    @Override
    public SMSSendResponseDto sendSMS(SMSSendRequestDto request) {
        try {
            IMessageWrapper message = messageService.sendSMSFromNumber(
                    request.recipient(), request.sender(),
                    request.messageContent());
            log.info("SMS sent successfully: {}", message);
            MessageDocument document = smsRepository.save(messageMapper.toDocument(message));
            log.info("SMS saved successfully: {}", document);
            return dtoDocumentMapper.toSMSSendResponseDto(document);
        } catch (DataAccessException e) {
            log.error("Failed to save SMS: {}", e.getMessage());
            throw new DomainException(
                    Error.INVALID_REQUEST.getCode(),
                    Error.INVALID_REQUEST.getStatus(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to schedule an SMS.
     * @param request the request object containing the details of the SMS to be scheduled
     * @return SMSScheduleResponseDto object containing the ID and status of the scheduled message
     */
    @Override
    public SMSScheduleResponseDto scheduleSMS(SMSScheduleRequestDto request) {
        try {
            IMessageWrapper message = messageService.scheduleSMS(
                    request.recipient(),
                    request.messageContent(),
                    request.scheduleTime());
            log.info("SMS scheduled successfully: {}", message);
            MessageDocument document = smsRepository.save(messageMapper.toDocument(message));
            document.setScheduledTime(request.scheduleTime());
            return dtoDocumentMapper.toSMSScheduleResponseDto(document);
        } catch (DataAccessException e) {
            log.error("Failed to save SMS: {}", e.getMessage());
            throw new DomainException(
                    Error.INVALID_REQUEST.getCode(),
                    Error.INVALID_REQUEST.getStatus(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("Failed to schedule SMS: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to send bulk SMS.
     * @param request the request object containing the details of the SMS to be sent to multiple recipients
     * @return SMSBulkSendResponseDto object containing the IDs and statuses of the sent messages
     */
    @Override
    public SMSBulkSendResponseDto sendBulkSMS(SMSBulkSendRequestDto request) {
        log.info("Sending bulk SMS: {}", request);
        try {
            List<SMSSendResponseDto> messages = new ArrayList<>();
            request.recipients().parallelStream().forEach(recipient -> {
                IMessageWrapper message = messageService.sendSMSFromService(
                        recipient,
                        request.messageContent());
                log.info("SMS sent successfully: {}", message);
                 MessageDocument document = smsRepository.save(messageMapper.toDocument(message));
                 messages.add(dtoDocumentMapper.toSMSSendResponseDto(document));
            });
            return new SMSBulkSendResponseDto(messages);
        } catch (DataAccessException e) {
            log.error("Failed to save SMS: {}", e.getMessage());
            throw new DomainException(
                    Error.INVALID_REQUEST.getCode(),
                    Error.INVALID_REQUEST.getStatus(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("Failed to bulk send SMS: {}", e.getMessage());
            throw e;
        }

    }

    /**
     * This method is used to schedule bulk SMS.
     * @param request the request object containing the details of the SMS to be scheduled for multiple recipients
     * @return SMSBulkScheduleResponseDto object containing the IDs and statuses of the scheduled messages
     */
    @Override
    public SMSBulkScheduleResponseDto scheduleBulkSMS(SMSBulkScheduleRequestDto request) {
        log.info("Scheduling bulk SMS: {}", request);
        try {
            List<SMSSendResponseDto> messages = new ArrayList<>();
            request.recipients().parallelStream().forEach(recipient -> {
                IMessageWrapper message = messageService.scheduleSMS(
                        recipient,
                        request.messageContent(),
                        request.scheduleTime());
                log.info("SMS scheduled successfully: {}", message);
                MessageDocument document = messageMapper.toDocument(message);
                document.setScheduledTime(request.scheduleTime());
                document = smsRepository.save(document);
                messages.add(dtoDocumentMapper.toSMSSendResponseDto(document));
            });
            return new SMSBulkScheduleResponseDto(messages, request.scheduleTime());
        } catch (DataAccessException e) {
            log.error("Failed to save bulk scheduled SMS: {}", e.getMessage());
            throw new DomainException(
                    Error.INVALID_REQUEST.getCode(),
                    Error.INVALID_REQUEST.getStatus(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("Failed to schedule bulk SMS: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * This method is used to update the status of an SMS.
     * @param smsStatus the object containing the ID and status of the SMS
     */
    @Override
    public void updateSMSStatus(IMessageStatusCallback smsStatus) {
        log.info("Updating SMS status: {}", smsStatus);
        try {
            MessageDocument document = smsRepository.findById(smsStatus.getMessageId())
                    .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
            MessageStatus status = MessageStatus.fromString(smsStatus.getStatus());
            if (status == MessageStatus.DELIVERED) {
                document.setDeliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
            }
            if (status == MessageStatus.FAILED || status == MessageStatus.UNDELIVERED) {
                document.setErrorCode(smsStatus.getErrorCode().orElse(null));
                document.setErrorMessage(smsStatus.getErrorMessage().orElse(null));
            }
            document.setStatus(status);
            smsRepository.save(document);
        } catch (DataAccessException e) {
            log.error("Failed to update SMS: {}", e.getMessage());
            throw new DomainException(
                    Error.INVALID_REQUEST.getCode(),
                    Error.INVALID_REQUEST.getStatus(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update SMS status: {}", e.getMessage());
            throw new DomainException(Error.UNEXPECTED_ERROR);
        }
    }

    /**
     * This method is used to poll the status of SMS messages.
     * It fetches all non-terminal messages from the database and updates their status.
     */
    @Override
    @Async("threadPoolTaskExecutor")
    @Scheduled(fixedDelay = 10000, timeUnit = TimeUnit.MILLISECONDS, initialDelay = 10000)
    public void pollSMSStatus() {
        if (messageService.pollMessageStatus()) {
            log.info("Polling SMS status");
            List<MessageDocument> nonTerminalMessages = smsRepository.findAllByStatus(NON_TERMINAL_STATUSES);
            log.info("Found {} Non-terminal messages: {}", nonTerminalMessages.size(), nonTerminalMessages);
            for (MessageDocument document : nonTerminalMessages) {
                try {
                    IMessageWrapper message = messageService.fetchMessageById(document.getId());
                    MessageStatus status = MessageStatus.fromString(message.getStatus().name());
                    if (status == document.getStatus()) {
                        continue;
                    }
                    if (status == MessageStatus.DELIVERED) {
                        document.setDeliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
                    }
                    if (status == MessageStatus.FAILED || status == MessageStatus.UNDELIVERED) {
                        document.setErrorCode(message.getErrorCode().orElse(null));
                        document.setErrorMessage(message.getErrorMessage().orElse(null));
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