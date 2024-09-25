package com.crm.smsmanagementservice.core.config;

import java.util.Arrays;

import com.crm.smsmanagementservice.core.util.ZonedDateTimeReadConverter;
import com.crm.smsmanagementservice.core.util.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

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
@Profile("!test")
public class MongoConfig {
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeReadConverter(),
                new ZonedDateTimeWriteConverter()
        ));
    }
}
