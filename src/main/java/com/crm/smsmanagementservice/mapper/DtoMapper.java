package com.crm.smsmanagementservice.mapper;

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
@Component("dtoMapper") @Mapper(componentModel = "spring")
public interface DtoMapper {

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    SMSSendResponseDto toSMSSendResponseDto(MessageDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduleTime", source = "scheduledTime")
    SMSScheduleResponseDto toSMSScheduleResponseDto(MessageDocument document);
}