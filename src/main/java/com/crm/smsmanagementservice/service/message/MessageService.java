package com.crm.smsmanagementservice.service.message;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.exception.DomainException;
import com.crm.smsmanagementservice.exception.Error;
import com.crm.smsmanagementservice.mapper.MessageMapper;
import com.crm.smsmanagementservice.repository.MessageRepository;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Service
@Slf4j(topic = "MessageService")
@RequiredArgsConstructor
public class MessageService implements IMessageService {
  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;

  @Override
  public MessageResponseDto saveMessage(IMessageWrapper message, String conversationId) {
    // TODO: SCHEDULED TIME NOT GETTING MAPPED
    MessageDocument messageDocument = messageMapper.toDocument(message);
    messageDocument.setConversationId(conversationId);
    log.info("Saving message: {}", messageDocument);
    return messageMapper.toResponseDto(messageRepository.save(messageDocument));
  }

  @Override
  public MessageResponseDto saveMessage(IMessageWrapper message, ZonedDateTime scheduledTime, String conversationId) {
    MessageDocument messageDocument = messageMapper.toDocument(message);
    messageDocument.setScheduledTime(scheduledTime);
    messageDocument.setConversationId(conversationId);
    log.info("Saving message: {}", messageDocument);
    return messageMapper.toResponseDto(messageRepository.save(messageDocument));
  }

  public MessageResponseDto updateMessageStatus(
      String id, MessageStatus status, String errorCode, String errorMessage) {
    MessageDocument message =
        messageRepository
            .findById(id)
            .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND));
    if (status == null || status == message.getStatus()) {
      return messageMapper.toResponseDto(message);
    }
    if (status == MessageStatus.DELIVERED) {
      message.setDeliveredTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
    }
    if (status == MessageStatus.FAILED || status == MessageStatus.UNDELIVERED) {
      message.setErrorCode(errorCode);
      message.setErrorMessage(errorMessage);
    }
    message.setStatus(status);
    return messageMapper.toResponseDto(messageRepository.save(message));
  }

  @Override
  public MessageResponseDto getMessageById(String id) {
    return messageMapper.toResponseDto(
        messageRepository
            .findById(id)
            .orElseThrow(() -> new DomainException(Error.ENTITY_NOT_FOUND)));
  }

  @Override
  public List<MessageResponseDto> getAllMessagesByStatus(List<MessageStatus> statuses) {
    return messageMapper.toResponseDtoList(messageRepository.findAllByStatus(statuses));
  }

  @Override
  public Page<MessageResponseDto> getMessagesByConversationId(
      String conversationId, Pageable pageable) {
    Page<MessageDocument> page = messageRepository.findByConversationId(conversationId, pageable);
    return page.map(messageMapper::toResponseDto);
  }
}
