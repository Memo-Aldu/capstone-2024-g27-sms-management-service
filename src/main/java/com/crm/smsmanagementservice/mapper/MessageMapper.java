package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class maps IMessageWrapper to MessageDocument.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "serviceSid", source = "serviceSid", qualifiedByName = "mapOptional")
    @Mapping(target = "errorCode", source = "errorCode", qualifiedByName = "mapOptional")
    @Mapping(target = "errorMessage", source = "errorMessage", qualifiedByName = "mapOptional")
    @Mapping(target = "segmentCount", source = "segmentCount", qualifiedByName = "mapOptional")
    @Mapping(target = "mediaCount", source = "mediaCount", qualifiedByName = "mapOptional")
    @Mapping(target = "mediaUrls", source = "mediaUrls", qualifiedByName = "mapOptionalMap")
    @Mapping(target = "sender", source = "sender", qualifiedByName = "mapOptional")
    @Mapping(target = "recipient", source = "recipient")
    @Mapping(target = "providerId", source = "providerId")
    @Mapping(target = "createdTime", source = "createdTime")
    @Mapping(target = "deliveredTime", source = "deliveredTime")
    @Mapping(target = "scheduledTime", source = "scheduledTime")
    @Mapping(target = "status", source = "status")
    MessageDocument toDocument(IMessageWrapper message);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "messageContent", source = "messageContent")
    @Mapping(target = "conversationId", source = "conversationId")
    @Mapping(target = "scheduledTime", source = "scheduledTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "deliveredTime", source = "deliveredTime")
    @Mapping(target = "mediaUrls", source = "mediaUrls")
    MessageResponseDto toResponseDto(MessageDocument document);

    List<MessageResponseDto> toResponseDtoList(List<MessageDocument> documents);

    @Named("mapOptional")
    default String mapOptional(Optional<String> value) {
        return value.orElse(null);
    }

    @Named("mapOptionalMap")
    default Map<String, String> mapOptionalMap(Optional<Map<String, String>> value) {
        return value.orElse(null);
    }
}