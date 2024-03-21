package com.crm.smsmanagementservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/1/2024, Friday
 */
@Configuration
public class CallbackProperties {
    @Value("${app.callback.url}")
    private String url;
    @Value("${app.callback.endpoints.smsStatus}")
    private String smsStatusEndpoint;
    @Value("${app.callback.endpoints.health}")
    private String healthEndpoint;
    @Setter @Getter
    private Boolean isHealthy;

    public String getSmsStatusEndpoint() {
        return url + smsStatusEndpoint;
    }

    public String getHealthEndpoint() {
        return url + healthEndpoint;
    }

}