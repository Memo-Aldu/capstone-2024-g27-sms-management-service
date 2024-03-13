package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.config.CallbackProperties;
import com.crm.smsmanagementservice.config.TwilioConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/13/2024, Wednesday
 */
@Service
@Slf4j
@AllArgsConstructor
public class HealthCheckService implements IHealthCheckService {
  private final RestTemplate restTemplate;
  private final CallbackProperties callbackProperties;
  private final TwilioConfig twilioConfig;

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void checkHealthAndDecideStrategy() {
    try {
      ResponseEntity<String> response =
          restTemplate.getForEntity(callbackProperties.getHealthEndpoint(), String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        log.info("Health check passed");
        setupCallback();
      } else {
        log.warn("Health check failed: {}", response.getStatusCode());
        revertToPolling();
      }
    } catch (Exception e) {
      log.warn("Health check failed: {}", e.getMessage());
      revertToPolling();
    }
  }

  private void setupCallback() {
    twilioConfig.setPollForStatus(false);
    log.info("Setting up callback");
  }

  private void revertToPolling() {
    log.info("Reverting to polling");
    twilioConfig.setPollForStatus(true);
  }
}
