package com.crm.smsmanagementservice.conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */

public interface ConversationExternalAPI {
    ConversationDTO getConversationById(String id);
    Page<ConversationDTO> getConversationsByUserId(String userId, Pageable pageable);
    ConversationDTO createConversation(ConversationDTO conversation);
    ConversationDTO updateConversation(String id, ConversationDTO conversation);
}
