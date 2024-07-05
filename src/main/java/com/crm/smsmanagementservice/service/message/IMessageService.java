package com.crm.smsmanagementservice.service.message;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
public interface IMessageService {
    MessageResponseDto saveMessage(IMessageWrapper messageWrapper, String conversationId);
    MessageResponseDto saveMessage(IMessageWrapper messageWrapper, ZonedDateTime scheduledTime, String conversationId);
    MessageResponseDto getMessageById(String id);
    List<MessageResponseDto> getAllMessagesByStatus(List<MessageStatus> statuses);
    Page<MessageResponseDto> getMessagesByConversationId(String conversationId, Pageable pageable);
    MessageResponseDto updateMessageStatus(String id, MessageStatus status, String errorCode, String errorMessage);
}