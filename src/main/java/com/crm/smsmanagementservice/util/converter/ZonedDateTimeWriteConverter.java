package com.crm.smsmanagementservice.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * This class converts a ZonedDateTime object to a Date object.
 * It implements the Converter interface.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }
}
