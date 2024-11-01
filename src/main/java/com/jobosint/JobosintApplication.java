package com.jobosint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableScheduling
@ConfigurationPropertiesScan("com.jobosint.config")
@SpringBootApplication
public class JobosintApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobosintApplication.class, args);
	}

}
