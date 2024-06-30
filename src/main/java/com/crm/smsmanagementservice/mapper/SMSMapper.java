package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/18/2024, Tuesday
 */
@Mapper(componentModel = "spring")
public interface SMSMapper {

  @Mapping(target = "messageId", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "conversationId", source = "conversationId")
  SMSSendResponseDto toSMSSendResponseDto(MessageResponseDto responseDto);

  @Mapping(target = "messageId", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "scheduledTime", source = "scheduledTime")
  @Mapping(target = "conversationId", source = "conversationId")
  SMSScheduleResponseDto toSMSScheduleResponseDto(MessageResponseDto responseDto);
}
