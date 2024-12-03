package com.crm.smsmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Profile("!test")
@Modulithic(
		sharedModules = {"com.crm.smsmanagementservice.core"},
		useFullyQualifiedModuleNames = true)
public class SmsManagementServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Toronto"));
		SpringApplication.run(SmsManagementServiceApplication.class, args);
	}
}
