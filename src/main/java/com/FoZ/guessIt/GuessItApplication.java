package com.FoZ.guessIt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableWebSecurity: smart enough to use spring security
public class GuessItApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuessItApplication.class, args);
	}

}
