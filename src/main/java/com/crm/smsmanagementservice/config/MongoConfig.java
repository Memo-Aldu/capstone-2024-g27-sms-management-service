package com.crm.smsmanagementservice.config;

import com.crm.smsmanagementservice.util.converter.ZonedDateTimeReadConverter;
import com.crm.smsmanagementservice.util.converter.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


import java.util.Arrays;

/**
 * This class is the configuration class for the MongoDB.
 * It creates a MongoCustomConversions bean.
 * It adds the ZonedDateTimeReadConverter and ZonedDateTimeWriteConverter to the customConversions.
 *
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
