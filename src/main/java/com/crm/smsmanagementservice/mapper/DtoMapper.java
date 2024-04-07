package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Mapper(componentModel = "spring")
public interface DtoMapper {

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    SMSSendResponseDto toSMSSendResponseDto(MessageDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduleTime", source = "scheduledTime")
    SMSScheduleResponseDto toSMSScheduleResponseDto(MessageDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    MMSSendResponseDto toMMSSendResponseDto(MessageDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduleTime", source = "scheduledTime")
    MMSScheduleResponseDto toMMSScheduleResponseDto(MessageDocument document);
}