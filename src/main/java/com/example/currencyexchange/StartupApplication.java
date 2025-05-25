package com.example.currencyexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StartupApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
	}

}
