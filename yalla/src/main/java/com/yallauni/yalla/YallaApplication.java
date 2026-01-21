package com.yallauni.yalla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main entry point for the Yalla Uni ride-sharing app.
 * This class bootstraps the Spring Boot application and enables
 * component scanning and auto-configuration.
 * 
 * @author Yalla Uni Team
 * @version 1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.yallauni.yalla.config")
public class YallaApplication {

	public static void main(String[] args) {
		SpringApplication.run(YallaApplication.class, args);
	}

}
