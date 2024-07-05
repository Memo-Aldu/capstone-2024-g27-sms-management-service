package com.crm.smsmanagementservice.service.conversation;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
public interface IConversationService {

    ConversationResponseDto createConversation(ConversationPostRequestDto requestDto);

    ConversationResponseDto getConversationById(String id);

    Page<ConversationResponseDto> getConversationsByParticipant(String participant, Pageable pageable);

    ConversationResponseDto getOrCreateConversation(String sender, String recipient);

    ConversationResponseDto updateConversation(String id, ConversationPatchRequestDto requestDto);

    void deleteConversation(String conversationId);
}
