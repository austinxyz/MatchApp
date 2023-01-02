package com.utr.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MatchAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(MatchAppApplication.class, args);
	}
}
