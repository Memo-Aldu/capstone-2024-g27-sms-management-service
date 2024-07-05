package com.crm.smsmanagementservice.service.mms;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.mapper.MMSMapper;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class is a service that handles operations related to the multimedia messaging service (MMS).
 * It has four methods: one for sending an MMS, one for scheduling an MMS, one for sending bulk MMS, and one for scheduling bulk MMS.
 * It uses the IMessagingService to perform these operations and the MessageRepository to save the messages.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/26/2024, Tuesday
 */

@Slf4j(topic = "MMSService")
@RequiredArgsConstructor@Service
public class MMSService implements IMMSService {
    private final MMSMapper mmsMapper;
    private final IMessageService messageService;
    private final IConversationService conversationService;
    private final IMessagingProviderService messagingProviderService;


    /**
     * This method is used to send an MMS.
     * @param mmsSendRequest the request object containing the details of the MMS to be sent
     * @return an MMSSendResponseDto object containing the ID and status of the sent message
     */
    @Override
    public MMSSendResponseDto sendMMS(MMSSendRequestDto mmsSendRequest) {
        IMessageWrapper message = messagingProviderService.sendMMSFromNumber(
                mmsSendRequest.recipient(), mmsSendRequest.sender(),
                mmsSendRequest.messageContent(), mmsSendRequest.mediaUrls());

        log.info("MMS sent with status {}", message.getStatus());

        ConversationResponseDto conversationDto = conversationService
                    .getOrCreateConversation(mmsSendRequest.sender(), mmsSendRequest.recipient());

        MessageResponseDto responseDto = messageService.saveMessage(message, conversationDto.id());
        return mmsMapper.toMMSSendResponseDto(responseDto);
    }

    /**
     * This method is used to schedule an MMS.
     * @param mmsScheduleRequest the request object containing the details of the MMS to be scheduled
     * @return an MMSScheduleResponseDto object containing the ID and status of the scheduled message
     */
    @Override
    public MMSScheduleResponseDto scheduleMMS(MMSScheduleRequestDto mmsScheduleRequest) {
        IMessageWrapper message = messagingProviderService.scheduleMMS(
                mmsScheduleRequest.recipient(), mmsScheduleRequest.messageContent(),
                mmsScheduleRequest.mediaUrls(), mmsScheduleRequest.scheduledTime()
        );
        log.info("MMS scheduled with status {}", message.getStatus());

        ConversationResponseDto conversationDto = conversationService
                    .getOrCreateConversation(mmsScheduleRequest.sender(), mmsScheduleRequest.recipient());

        MessageResponseDto responseDto = messageService.saveMessage(message,
                mmsScheduleRequest.scheduledTime() ,conversationDto.id());
        return mmsMapper.toMMSScheduleResponseDto(responseDto);
    }

    /**
     * This method is used to send bulk MMS.
     * @param mmsBulkRequest the request object containing the details of the MMS to be sent to multiple recipients
     * @return an MMSBulkSendResponseDto object containing the IDs and statuses of the sent messages
     */
    @Override
    public MMSBulkSendResponseDto sendBulkMMS(MMSBulkSendRequestDto mmsBulkRequest) {
        List<MMSSendResponseDto> messages = mmsBulkRequest.recipients().parallelStream()
                .map(recipient -> {
                    IMessageWrapper message = messagingProviderService.sendMMSFromService(
                            recipient, mmsBulkRequest.messageContent(), mmsBulkRequest.mediaUrls());
                    log.info("MMS sent with status {}", message.getStatus());
                    // Note: The conversationId is not passed in the request, so we need to search the db
                    ConversationResponseDto conversationDto = conversationService
                            .getOrCreateConversation(mmsBulkRequest.sender(), recipient);

                    MessageResponseDto responseDto = messageService.saveMessage(message, conversationDto.id());
                    return mmsMapper.toMMSSendResponseDto(responseDto);
                })
                .toList();
        return new MMSBulkSendResponseDto(messages);
    }

    /**
     * This method is used to schedule bulk MMS.
     * @param mmsBulkScheduleRequest the request object containing the details of the MMS to be scheduled for multiple recipients
     * @return an MMSBulkScheduleResponseDto object containing the IDs and statuses of the scheduled messages
     */
    @Override
    public MMSBulkScheduleResponseDto scheduleBulkMMS(MMSBulkScheduleRequestDto mmsBulkScheduleRequest) {
        List<MMSSendResponseDto> messages = mmsBulkScheduleRequest.recipients().parallelStream()
                .map(recipient -> {
                    IMessageWrapper message = messagingProviderService.scheduleMMS(
                            recipient, mmsBulkScheduleRequest.messageContent(),
                            mmsBulkScheduleRequest.mediaUrls(),
                            mmsBulkScheduleRequest.scheduledTime()
                    );
                    log.info("MMS scheduled with status {}", message.getStatus());
                    // Note: The conversationId is not passed in the request, so we need to search the db
                    ConversationResponseDto conversationDto = conversationService
                            .getOrCreateConversation(mmsBulkScheduleRequest.sender(), recipient);

                    MessageResponseDto responseDto = messageService.saveMessage(message,
                            mmsBulkScheduleRequest.scheduledTime() ,conversationDto.id());
                    return mmsMapper.toMMSSendResponseDto(responseDto);
                })
                .toList();
        return new MMSBulkScheduleResponseDto(messages, mmsBulkScheduleRequest.scheduledTime());
    }
}
