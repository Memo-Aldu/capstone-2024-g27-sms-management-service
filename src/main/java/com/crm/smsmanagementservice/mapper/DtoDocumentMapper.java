package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.SmSDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Component("dtoDocumentMapper") @Mapper(componentModel = "spring")
public interface DtoDocumentMapper {
/*
    DtoDocumentMapper INSTANCE = Mappers.getMapper(DtoDocumentMapper.class);
*/
    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    SMSSendResponseDto toSMSSendResponseDto(SmSDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduleTime", source = "scheduledTime")
    SMSScheduleResponseDto toSMSScheduleResponseDto(SmSDocument document);
}