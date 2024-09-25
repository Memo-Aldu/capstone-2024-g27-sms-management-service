package com.crm.smsmanagementservice.config;


import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/5/2024, Wednesday
 */

public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {

    @Override
    public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
        writer.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    @Override
    public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return ZonedDateTime.parse(reader.readString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }
}
