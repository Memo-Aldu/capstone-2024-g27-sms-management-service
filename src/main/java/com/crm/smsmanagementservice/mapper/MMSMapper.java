package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/18/2024, Tuesday
 */
@Mapper(componentModel = "spring")
public interface MMSMapper {
    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "conversationId", source = "conversationId")
    MMSSendResponseDto toMMSSendResponseDto(MessageResponseDto responseDto);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduledTime", source = "scheduledTime")
    @Mapping(target = "conversationId", source = "conversationId")
    MMSScheduleResponseDto toMMSScheduleResponseDto(MessageResponseDto responseDto);

}
