package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.config.CallbackProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CallbackProperties callbackProperties;

    private HealthCheckService healthCheckService;

    @BeforeEach
    void setUp() {
        healthCheckService = new HealthCheckService(restTemplate, callbackProperties);
    }

    @Test
    void whenHealthCheckPasses_thenSetupCallback() {
        when(restTemplate.getForEntity(eq(callbackProperties.getHealthEndpoint()), eq(String.class)))
            .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));
        // When
        healthCheckService.checkHealthAndDecideStrategy();
        // Then
        verify(callbackProperties).setIsHealthy(true);
    }

    @Test
    void whenHealthCheckFails_thenRevertToPolling() {
        // Given
        when(restTemplate.getForEntity(eq(callbackProperties.getHealthEndpoint()), eq(String.class)))
            .thenReturn(new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST));

        // When
        healthCheckService.checkHealthAndDecideStrategy();

        // Then
        verify(callbackProperties).setIsHealthy(false);
    }

    @Test
    void whenHealthCheckThrowsException_thenRevertToPolling() {
        // Given
        when(restTemplate.getForEntity(eq(callbackProperties.getHealthEndpoint()), eq(String.class)))
            .thenThrow(new RuntimeException("Service Unavailable"));

        // When
        healthCheckService.checkHealthAndDecideStrategy();

        // Then
        verify(callbackProperties).setIsHealthy(false);
    }
}
