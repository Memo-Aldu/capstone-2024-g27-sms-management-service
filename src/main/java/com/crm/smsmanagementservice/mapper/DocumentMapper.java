package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.service.message.IMessageWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "serviceSid", source = "serviceSid", qualifiedByName = "mapOptional")
    @Mapping(target = "errorCode", source = "errorCode", qualifiedByName = "mapOptional")
    @Mapping(target = "errorMessage", source = "errorMessage", qualifiedByName = "mapOptional")
    @Mapping(target = "segmentCount", source = "segmentCount", qualifiedByName = "mapOptional")
    @Mapping(target = "mediaCount", source = "mediaCount", qualifiedByName = "mapOptional")
    @Mapping(target = "mediaUrls", source = "mediaUrls", qualifiedByName = "mapOptionalMap")
    @Mapping(target = "sender", source = "sender", qualifiedByName = "mapOptional")
    MessageDocument toDocument(IMessageWrapper message);

    @Named("mapOptional")
    default String mapOptional(Optional<String> value) {
        return value.orElse(null);
    }

    @Named("mapOptionalMap")
    default Map<String, String> mapOptionalMap(Optional<Map<String, String>> value) {
        return value.orElse(null);
    }
}