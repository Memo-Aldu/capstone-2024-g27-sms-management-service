package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.twilio.type.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import com.twilio.rest.api.v2010.account.Message;

import java.util.List;
import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Component @Mapper(componentModel = "spring")
public interface MessageDocumentMapper {
    @Mapping(target = "id", source = "sid")
    @Mapping(target = "sender", source = "from", qualifiedByName = "mapNumber")
    @Mapping(target = "recipient", source = "to")
    @Mapping(target = "messageContent", source = "body")
    @Mapping(target = "createdTime", source = "dateCreated")
    @Mapping(target = "deliveredTime", source = "dateSent")
    @Mapping(target ="scheduledTime", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "providerId", source = "sid")
    @Mapping(target = "errorCode", source = "errorCode")
    @Mapping(target = "errorMessage", source = "errorMessage")
    @Mapping(target = "segmentCount", source = "numSegments")
    @Mapping(target = "mediaCount", source = "numMedia")
    @Mapping(target = "mediaUrls", source = "subresourceUris", qualifiedByName = "mapMediaUrls")
    SmSDocument toDocument(Message message);

    @Named("mapNumber")
     default String mapNumber(PhoneNumber value) {
        return value.getEndpoint();
    }

    @Named("mapStatus")
    default MessageStatus mapStatus(Message.Status status) {
        return MessageStatus.valueOf(status.name());
    }

    @Named("mapMediaUrls")
    default List<String> mapMediaUrls(Map<String, String> subresourceUris) {
        return subresourceUris.values().stream().toList();
    }
}