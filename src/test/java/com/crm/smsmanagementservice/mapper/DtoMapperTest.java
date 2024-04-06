package com.crm.smsmanagementservice.mapper;

import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;



/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/18/2024, Monday
 */


public class DtoDocumentMapperTest {

    private final DtoMapper documentMapper = new DtoMapperImpl();
    private final DocumentMapper messageMapper = new DocumentMapperImpl();
    @Test
    void toSMSSendResponseDto_NullDocument_ReturnsNull() {
        assertNull(documentMapper.toSMSSendResponseDto(null));
    }

    @Test
    void toSMSSendResponseDto_ValidDocument_MapsCorrectly() {
        // Given
        MessageDocument document = MessageDocument.builder()
                .id("123").status(MessageStatus.DELIVERED).build();
        SMSSendResponseDto dto = documentMapper.toSMSSendResponseDto(document);
        assertNotNull(dto);
        assertEquals("123", dto.messageId());
        assertEquals(MessageStatus.DELIVERED, dto.status());
    }

    @Test
    void toSMSScheduleResponseDto_NullDocument_ReturnsNull() {
        assertNull(documentMapper.toSMSScheduleResponseDto(null));
    }

    @Test
    void toSMSScheduleResponseDto_ValidDocument_MapsCorrectly() {
        // Given
        ZonedDateTime scheduledTime = ZonedDateTime.now();
        MessageDocument document = MessageDocument.builder()
                .id("456").status(MessageStatus.SCHEDULED).scheduledTime(scheduledTime).build();

        SMSScheduleResponseDto dto = documentMapper.toSMSScheduleResponseDto(document);

        assertNotNull(dto);
        assertEquals("456", dto.messageId());
        assertEquals(MessageStatus.SCHEDULED, dto.status());
        assertEquals(scheduledTime, dto.scheduleTime());
    }
}

