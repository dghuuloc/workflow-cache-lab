package com.porters.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.porters")
public class WorkflowCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowCacheApplication.class, args);
	}

}
