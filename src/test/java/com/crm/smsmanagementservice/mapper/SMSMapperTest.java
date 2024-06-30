package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/18/2024, Tuesday
 */
public class SMSMapperTest {
  private final SMSMapper smsMapper = new SMSMapperImpl();

  @Test
  void toSMSSendResponseDto_NullDocument_ReturnsNull() {
    assertNull(smsMapper.toSMSSendResponseDto(null));
  }

  @Test
  void toSMSSendResponseDto_ValidDocument_MapsCorrectly() {
    // Given
    MessageResponseDto responseDto =
        MessageResponseDto.builder().id("123").status(MessageStatus.DELIVERED).build();
    SMSSendResponseDto dto = smsMapper.toSMSSendResponseDto(responseDto);
    assertNotNull(dto);
    assertEquals("123", dto.messageId());
    assertEquals(MessageStatus.DELIVERED, dto.status());
  }

  @Test
  void toSMSScheduleResponseDto_NullDocument_ReturnsNull() {
    assertNull(smsMapper.toSMSScheduleResponseDto(null));
  }

  @Test
  void toSMSScheduleResponseDto_ValidDocument_MapsCorrectly() {
    // Given
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    MessageResponseDto responseDto =
        MessageResponseDto.builder()
            .id("456")
            .status(MessageStatus.SCHEDULED)
            .scheduledTime(scheduledTime)
            .build();

    SMSScheduleResponseDto dto = smsMapper.toSMSScheduleResponseDto(responseDto);

    assertNotNull(dto);
    assertEquals("456", dto.messageId());
    assertEquals(MessageStatus.SCHEDULED, dto.status());
    assertEquals(scheduledTime, dto.scheduledTime());
  }
}
