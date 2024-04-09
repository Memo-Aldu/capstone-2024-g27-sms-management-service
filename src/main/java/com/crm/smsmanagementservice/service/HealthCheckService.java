package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.config.CallbackProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This class is a service that checks the health of the application.
 * It has a method that is triggered when the application is ready, which checks the health of the application and decides whether to set up a callback or revert to polling.
 * It uses the RestTemplate to make the health check request and the CallbackProperties to store the result of the health check.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/13/2024, Wednesday
 */
@Service
@Slf4j(topic = "HealthCheckService")
@AllArgsConstructor
public class HealthCheckService implements IHealthCheckService {
  private final RestTemplate restTemplate;
  private final CallbackProperties callbackProperties;


  /**
   * This method is triggered when the application is ready.
   * It checks the health of the application by making a GET request to the health endpoint.
   * If the response status is 2xx, it logs that the health check passed and calls the setupCallback method.
   * If the response status is not 2xx, or if an exception is thrown, it logs that the health check failed and calls the revertToPolling method.
   */
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

  /**
   * This method is called when the health check passes.
   * It sets the isHealthy property of the CallbackProperties to true and logs that the callback is being set up.
   */
  private void setupCallback() {
    callbackProperties.setIsHealthy(true);
    log.info("Setting up callback");
  }

  /**
   * This method is called when the health check fails.
   * It sets the isHealthy property of the CallbackProperties to false and logs that the application is reverting to polling.
   */
  private void revertToPolling() {
    log.info("Reverting to polling");
    callbackProperties.setIsHealthy(false);
  }
}
