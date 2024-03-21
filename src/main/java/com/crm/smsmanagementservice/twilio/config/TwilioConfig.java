package com.crm.smsmanagementservice.twilio.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
**/

@Getter
@Configuration
@Slf4j(topic = "TwilioConfig")
@ConfigurationProperties(prefix = "twilio")
@AllArgsConstructor @NoArgsConstructor
public class TwilioConfig {
    @Value("${twilio.accountSid}")
    private String accountSid;
    @Value("${twilio.authToken}")
    private String authToken;
    @Value("${twilio.trialNumber}")
    private String trialNumber;
    @Value("${twilio.service.schedulingSMSSid}")
    private String schedulingServiceSid;
    @Value("${twilio.service.bulkSMSSid}")
    private String bulkServiceSid;
    @Setter
    private boolean pollForStatus;

    @PostConstruct
    private void init() {
        Twilio.init(this.accountSid, this.authToken);
        log.info("Twilio initialized");
    }
}