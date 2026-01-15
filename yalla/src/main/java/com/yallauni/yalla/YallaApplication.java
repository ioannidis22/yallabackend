package com.yallauni.yalla;

// Main Spring Boot class to launch the application
import org.springframework.boot.SpringApplication;
// Annotation for Spring Boot auto-configuration
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Annotation to enable scanning for @ConfigurationProperties classes
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.yallauni.yalla.config")
public class YallaApplication {

	public static void main(String[] args) {
		SpringApplication.run(YallaApplication.class, args);
	}

}
