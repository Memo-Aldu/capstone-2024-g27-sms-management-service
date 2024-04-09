package com.crm.smsmanagementservice.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * This class converts a Date object to a ZonedDateTime object.
 * It implements the Converter interface.
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */
public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC);
    }
}
