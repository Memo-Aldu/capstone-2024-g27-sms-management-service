package com.crm.smsmanagementservice.config;

import com.crm.smsmanagementservice.core.util.ZonedDateTimeReadConverter;
import com.crm.smsmanagementservice.core.util.ZonedDateTimeWriteConverter;
import com.mongodb.MongoClientSettings;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.Arrays;

@TestConfiguration
@EnableMongoRepositories(basePackages = "com.crm.smsmanagementservice.*.persistence")
public class EmbeddedMongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "testdb";
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new ZonedDateTimeReadConverter(),
                new ZonedDateTimeWriteConverter()
        ));
    }

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        CodecRegistry customCodecRegistry = CodecRegistries.fromRegistries(
                defaultCodecRegistry,
                CodecRegistries.fromCodecs(new ZonedDateTimeCodec())
        );

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(customCodecRegistry)
                .build();

        return MongoClients.create(clientSettings);
    }
}