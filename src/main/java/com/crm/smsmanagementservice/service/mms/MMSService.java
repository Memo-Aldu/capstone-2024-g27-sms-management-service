package com.crm.smsmanagementservice.service.mms;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.mapper.DocumentMapper;
import com.crm.smsmanagementservice.mapper.DtoMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import com.crm.smsmanagementservice.service.message.IMessagingService;
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
    private final DtoMapper dtoMapper;
    private final DocumentMapper messageDocumentMapper;
    private final IMessagingService messageService;
    private final MessageRepository smsRepository;

    /**
     * This method is used to send an MMS.
     * @param mmsSendRequest the request object containing the details of the MMS to be sent
     * @return an MMSSendResponseDto object containing the ID and status of the sent message
     */
    @Override
    public MMSSendResponseDto sendMMS(MMSSendRequestDto mmsSendRequest) {
        IMessageWrapper message = messageService.sendMMSFromNumber(
                mmsSendRequest.recipient(), mmsSendRequest.sender(),
                mmsSendRequest.messageContent(), mmsSendRequest.mediaUrls());
        log.info("MMS sent with status {}", message.getStatus());
        MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
        return dtoMapper.toMMSSendResponseDto(document);
    }

    /**
     * This method is used to schedule an MMS.
     * @param mmsScheduleRequest the request object containing the details of the MMS to be scheduled
     * @return an MMSScheduleResponseDto object containing the ID and status of the scheduled message
     */
    @Override
    public MMSScheduleResponseDto scheduleMMS(MMSScheduleRequestDto mmsScheduleRequest) {
        IMessageWrapper message = messageService.scheduleMMS(
                mmsScheduleRequest.recipient(), mmsScheduleRequest.messageContent(),
                mmsScheduleRequest.mediaUrls(), mmsScheduleRequest.scheduleTime()
        );
        log.info("MMS scheduled with status {}", message.getStatus());
        MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
        return dtoMapper.toMMSScheduleResponseDto(document);
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
                    IMessageWrapper message = messageService.sendMMSFromService(
                            recipient, mmsBulkRequest.messageContent(), mmsBulkRequest.mediaUrls());
                    log.info("MMS sent with status {}", message.getStatus());
                    MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
                    return dtoMapper.toMMSSendResponseDto(document);
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
                    IMessageWrapper message = messageService.scheduleMMS(
                            recipient, mmsBulkScheduleRequest.messageContent(),
                            mmsBulkScheduleRequest.mediaUrls(),
                            mmsBulkScheduleRequest.scheduleTime()
                    );
                    log.info("MMS scheduled with status {}", message.getStatus());
                    MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
                    return dtoMapper.toMMSSendResponseDto(document);
                })
                .toList();
        return new MMSBulkScheduleResponseDto(messages, mmsBulkScheduleRequest.scheduleTime());
    }
}
