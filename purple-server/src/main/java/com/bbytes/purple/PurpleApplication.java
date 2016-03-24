package com.bbytes.purple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;

@EnableAutoConfiguration(exclude={EmbeddedMongoAutoConfiguration.class})
@SpringBootApplication
public class PurpleApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(PurpleApplication.class, args);
	}
}
