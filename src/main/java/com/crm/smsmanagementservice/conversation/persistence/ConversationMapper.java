package com.crm.smsmanagementservice.conversation.persistence;

import com.crm.smsmanagementservice.conversation.ConversationDTO;
import org.mapstruct.Mapper;

/**
 * This class maps MessageDocument to the appropriate response DTOs.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationDTO toDTO(ConversationDocument document);
    ConversationDocument toDocument(ConversationDTO conversationPostRequestDto);
}