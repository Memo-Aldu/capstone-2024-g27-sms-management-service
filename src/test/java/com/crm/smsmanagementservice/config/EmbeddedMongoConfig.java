package com.crm.smsmanagementservice.config;

import com.crm.smsmanagementservice.core.util.ZonedDateTimeReadConverter;
import com.crm.smsmanagementservice.core.util.ZonedDateTimeWriteConverter;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@TestConfiguration
@Profile("test")
@EnableMongoRepositories(basePackages = "com.crm.smsmanagementservice.*.persistence")
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
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
                CodecRegistries.fromCodecs(new ZonedDateTimeCodec()) // Ensure proper codec registration
        );

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(customCodecRegistry)
                .build();

        return MongoClients.create(clientSettings);
    }
}