package com.crm.smsmanagementservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/13/2024, Wednesday
 */

@Configuration
public class HttpConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
