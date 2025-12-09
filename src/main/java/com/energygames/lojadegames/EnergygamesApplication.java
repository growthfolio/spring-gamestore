package com.energygames.lojadegames;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnergygamesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergygamesApplication.class, args);
	}

}
