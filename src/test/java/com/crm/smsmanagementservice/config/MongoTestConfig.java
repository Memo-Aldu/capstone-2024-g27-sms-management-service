package com.crm.smsmanagementservice.config;


import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/19/2024, Tuesday
 */

@Configuration
@Profile("test") @Slf4j
public class MongoTestConfig {
    private final int TEST_PORT = 27019;
    private final String HOST = "localhost";


  @Bean
  public MongoTemplate mongoTemplate() throws IOException {
        log.info("Starting embedded mongo server");


        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodConfig mongodConfig = MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(HOST, TEST_PORT , Network.localhostIsIPv6()))
            .build();

        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();

        return new MongoTemplate(MongoClients.create(String.format("mongodb://%s:%d", HOST, TEST_PORT)), "test-db");
    }
}
