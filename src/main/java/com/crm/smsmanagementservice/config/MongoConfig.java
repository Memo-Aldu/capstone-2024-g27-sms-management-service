package com.crm.smsmanagementservice.config;

import com.crm.smsmanagementservice.util.converter.ZonedDateTimeReadConverter;
import com.crm.smsmanagementservice.util.converter.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


import java.util.Arrays;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/25/2024, Sunday
 */

@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeReadConverter(),
                new ZonedDateTimeWriteConverter()
        ));
    }
}
