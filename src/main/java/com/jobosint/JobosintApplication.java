package com.jobosint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableFeignClients
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan("com.jobosint.config")
@SpringBootApplication
public class JobosintApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobosintApplication.class, args);
	}

}
