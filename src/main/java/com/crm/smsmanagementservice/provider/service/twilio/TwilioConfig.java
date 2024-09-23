package com.crm.smsmanagementservice.provider.service.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
**/

@Setter
@Configuration
@Slf4j(topic = "TwilioConfig")
@ConfigurationProperties(prefix = "twilio")
@AllArgsConstructor @NoArgsConstructor
public class TwilioConfig {
    @Value("${twilio.accountSid}")
    private String accountSid;
    @Value("${twilio.authToken}")
    private String authToken;
    @Value("${twilio.number}")
    @Getter private String twilioNumber;
    @Value("${twilio.service.schedulingSMSSid}")
    @Getter private String schedulingServiceSid;
    @Value("${twilio.service.bulkSMSSid}")
    @Getter private String bulkServiceSid;
    @Value("${twilio.testAccountSid}")
    private String testAccountSid;
    @Value("${twilio.testAuthToken}")
    private String testAuthToken;
    @Value("${twilio.callback.url}")
    private String url;
    @Value("${twilio.callback.endpoints.smsStatus}")
    private String smsStatusEndpoint;
    @Value("${twilio.callback.endpoints.inboundMessage}")
    private String inboundMessageEndpoint;
    @Value("${twilio.useTestAccount}")
    private boolean useTestAccount;


    public String getStatusCallbackUrl() {
        return this.url + this.smsStatusEndpoint;
    }

    @PostConstruct
    private void init() throws URISyntaxException {
        if (this.useTestAccount) {
            log.info("Using test account");
            log.info("Test account sid: {}", this.testAccountSid);
            log.info("Test auth token: {}", this.testAuthToken);
            Twilio.init(this.testAccountSid, this.testAuthToken);
        } else {
            log.info("Using twilio production account");
            Twilio.init(this.accountSid, this.authToken);
            setUpNumber();
        }
        log.info("Twilio configuration initialized");
    }

    private void setUpNumber() throws URISyntaxException {
        IncomingPhoneNumber phoneNumber = IncomingPhoneNumber.reader()
                .setPhoneNumber(new com.twilio.type.PhoneNumber(this.twilioNumber))
                .read().iterator().next();

        boolean isNumberSet = phoneNumber.getSmsUrl().equals(new URI(this.getInboundMessageUrl())) &&
                phoneNumber.getSmsFallbackUrl().equals(new URI(this.getStatusCallbackUrl()));

        if (isNumberSet) {
            log.info("Twilio number already set up");
            return;
        }

        log.info("Setting up twilio number: {}", this.twilioNumber);
        IncomingPhoneNumber.updater(phoneNumber.getSid())
                .setSmsUrl(new URI(this.getInboundMessageUrl()))
                .setSmsFallbackUrl(new URI(this.getStatusCallbackUrl()))
                .update();
    }

    private String getInboundMessageUrl() {
        return this.url + this.inboundMessageEndpoint;
    }
}
