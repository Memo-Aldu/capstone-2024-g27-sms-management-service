package com.crm.smsmanagementservice.service.sms;

import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.mapper.SMSMapper;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private final SMSMapper smsMapper;
    private final IMessagingProviderService messagingProviderService;
    private final IMessageService messageService;
    private final IConversationService conversationService;

    /**
     * This method is used to send an SMS.
     * @param request the request object containing the details of the SMS to be sent
     * @return SMSSendResponseDto object containing the ID and status of the sent message
     */
    @Override
    public SMSSendResponseDto sendSMS(SMSSendRequestDto request) {
        try {
            IMessageWrapper message = messagingProviderService.sendSMSFromNumber(
                    request.recipient(), request.sender(),
                    request.messageContent());
            log.info("SMS sent successfully: {}", message);
            ConversationResponseDto conversationDto = conversationService
                       .getOrCreateConversation(request.sender(), request.recipient());

            MessageResponseDto responseDto = messageService.saveMessage(message, conversationDto.id());
            log.info("SMS saved successfully: {}", responseDto);
            return smsMapper.toSMSSendResponseDto(responseDto);
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
            IMessageWrapper message = messagingProviderService.scheduleSMS(
                    request.recipient(),
                    request.messageContent(),
                    request.scheduledTime());
            log.info("SMS scheduled successfully: {}", message);
            ConversationResponseDto conversationDto = conversationService
                       .getOrCreateConversation(request.sender(), request.recipient());
            MessageResponseDto responseDto = messageService.saveMessage(message, request.scheduledTime(), conversationDto.id());
            return smsMapper.toSMSScheduleResponseDto(responseDto);
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
                IMessageWrapper message = messagingProviderService.sendSMSFromService(
                        recipient,
                        request.messageContent());
                log.info("SMS sent successfully: {}", message);

                ConversationResponseDto conversationResponseDto = conversationService
                        .getOrCreateConversation(request.sender(), recipient);

                MessageResponseDto responseDto = messageService.saveMessage(message, conversationResponseDto.id());
                messages.add(smsMapper.toSMSSendResponseDto(responseDto));
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
                IMessageWrapper message = messagingProviderService.scheduleSMS(
                        recipient,
                        request.messageContent(),
                        request.scheduledTime());
                log.info("SMS scheduled successfully: {}", message);
                ConversationResponseDto conversationDto = conversationService
                    .getOrCreateConversation(request.sender(), recipient);

                MessageResponseDto responseDto = messageService.saveMessage(message, request.scheduledTime(), conversationDto.id());
                messages.add(smsMapper.toSMSSendResponseDto(responseDto));
            });
            return new SMSBulkScheduleResponseDto(messages, request.scheduledTime());
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
}