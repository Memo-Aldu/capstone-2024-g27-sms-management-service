package com.crm.smsmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Modulithic(
		sharedModules = {"com.crm.smsmanagementservice.core"},
		useFullyQualifiedModuleNames = true)
public class SmsManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsManagementServiceApplication.class, args);
	}
}
