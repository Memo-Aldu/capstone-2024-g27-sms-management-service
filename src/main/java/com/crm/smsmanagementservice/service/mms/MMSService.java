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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
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


    @Override
    public MMSSendResponseDto sendMMS(MMSSendRequestDto mmsSendRequest) {
        IMessageWrapper message = messageService.sendMMSFromNumber(
                mmsSendRequest.recipient(), mmsSendRequest.messageContent(), mmsSendRequest.mediaUrls());
        log.info("MMS sent with status {}", message.getStatus());
        MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
        return dtoMapper.toMMSSendResponseDto(document);
    }

    @Override
    public MMSScheduleResponseDto scheduleMMS(MMSScheduleRequestDto mmsScheduleRequest) {
        IMessageWrapper message = messageService.scheduleMMS(
                mmsScheduleRequest.recipient(), mmsScheduleRequest.messageContent(),
                mmsScheduleRequest.mediaUrls(),
                mmsScheduleRequest.scheduleTime()
        );
        log.info("MMS scheduled with status {}", message.getStatus());
        MessageDocument document = smsRepository.save(messageDocumentMapper.toDocument(message));
        return dtoMapper.toMMSScheduleResponseDto(document);
    }

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
