package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.SmSDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
@Component @Mapper(componentModel = "spring")
public interface DtoDocumentMapper {

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Named("toSMSSendResponseDto")
    SMSSendResponseDto toSMSSendResponseDto(SmSDocument document);

    @Mapping(target = "messageId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "scheduleTime", source = "scheduledTime")
    SMSScheduleResponseDto toSMSScheduleResponseDto(SmSDocument document);
}