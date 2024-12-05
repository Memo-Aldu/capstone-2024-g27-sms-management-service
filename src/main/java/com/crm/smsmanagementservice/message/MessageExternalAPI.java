package com.crm.smsmanagementservice.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
public interface MessageExternalAPI {
    MessageDTO getMessageById(String messageId);
    Page<MessageDTO> getMessageByParticipantId(String userId, String contactId, Pageable pageable);
    Page<MessageDTO> getMessagesByUserId(String userId, Pageable pageable);
    List<MessageDTO> createMessage(List<MessageDTO> messageCreateDTO);
    MessageDTO cancelMessage(String messageId);
}