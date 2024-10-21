package com.crm.smsmanagementservice.message.persistence;

import com.crm.smsmanagementservice.core.dto.DomainMessage;
import com.crm.smsmanagementservice.message.MessageDTO;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * This class maps IDomainMessage to MessageDocument.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "media", source = "media", qualifiedByName = "mapMedia")
    MessageDTO toDTO(MessageDocument document);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "scheduledDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "media", source = "mediaUrls", qualifiedByName = "mapOptionalMap")

    @Mapping(target = "resourceId", source = "id")
    @Mapping(target = "to", source = "recipient")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "content", source = "messageContent")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "direction", source = "direction")
    @Mapping(target = "deliveredTime", source = "deliveredTime")
    @Mapping(target = "resourceUri", source = "URI")
    @Mapping(target = "providerId", source = "providerId")
    @Mapping(target = "from", source = "sender", qualifiedByName = "mapOptional")
    @Mapping(target = "messageSegmentCount", source = "segmentCount", qualifiedByName = "mapOptionalToInteger")
    @Mapping(target = "serviceSid", source = "serviceSid", qualifiedByName = "mapOptional")
    @Mapping(target = "errorCode", source = "errorCode", qualifiedByName = "mapOptional")
    @Mapping(target = "errorMessage", source = "errorMessage", qualifiedByName = "mapOptional")
    @Mapping(target = "apiVersion", source = "apiVersion", qualifiedByName = "mapOptional")
    MessageDocument toDocument(DomainMessage message);


    @Named("mapOptional")
    default String mapOptional(Optional<String> value) {
        return value.orElse(null);
    }

    @Named("mapOptionalToInteger")
    default Integer mapOptionalToInteger(Optional<String> value) {
        return value.map(Integer::parseInt).orElse(null);
    }

    @Named("mapOptionalMap")
    default Map<String, String> mapOptionalMap(Optional<Map<String, String>> value) {
        return value.orElse(new HashMap<>());
    }

    @Named("mapMedia")
    default List<String> mapMedia(Map<String, String> media) {
        if (media == null) {
            return new ArrayList<>();
        }
        return media.values().stream().toList();
    }
}