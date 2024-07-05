package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;
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
public class MMSMapperTest {
    private final MMSMapper mmsMapper = new MMSMapperImpl();

    @Test
    void toMMSScheduleResponseDto_NullDocument_ReturnsNull() {
        assertNull(mmsMapper.toMMSScheduleResponseDto(null));
    }

    @Test
    void toMMSScheduleResponseDto_ValidDocument_MapsCorrectly() {
        // Given
        ZonedDateTime scheduledTime = ZonedDateTime.now();
        MessageResponseDto responseDto =
                MessageResponseDto.builder()
                        .id("456")
                        .status(MessageStatus.SCHEDULED)
                        .scheduledTime(scheduledTime)
                        .build();

        MMSScheduleResponseDto dto = mmsMapper.toMMSScheduleResponseDto(responseDto);

        assertNotNull(dto);
        assertEquals("456", dto.messageId());
        assertEquals(MessageStatus.SCHEDULED, dto.status());
        assertEquals(scheduledTime, dto.scheduledTime());
    }

    @Test
    void toMMSSendResponseDto_NullDocument_ReturnsNull() {
        assertNull(mmsMapper.toMMSSendResponseDto(null));
    }

    @Test
    void toMMSSendResponseDto_ValidDocument_MapsCorrectly() {
        // Given
        MessageResponseDto responseDto =
                MessageResponseDto.builder().id("123").status(MessageStatus.DELIVERED).build();
        MMSSendResponseDto dto = mmsMapper.toMMSSendResponseDto(responseDto);
        assertNotNull(dto);
        assertEquals("123", dto.messageId());
        assertEquals(MessageStatus.DELIVERED, dto.status());
    }
}
