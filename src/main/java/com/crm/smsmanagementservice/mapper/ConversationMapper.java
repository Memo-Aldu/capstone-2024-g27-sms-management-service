package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;

import com.crm.smsmanagementservice.entity.ConversationDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * This class maps MessageDocument to the appropriate response DTOs.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper {

    ConversationResponseDto toConversationResponseDto(ConversationDocument document);

    @Mapping(target = "updatedTime", ignore = true) @Mapping(target = "createdTime", ignore = true)
    ConversationDocument toDocument(ConversationPostRequestDto conversationPostRequestDto);

}