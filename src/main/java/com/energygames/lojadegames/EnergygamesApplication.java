package com.energygames.lojadegames;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnergygamesApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(EnergygamesApplication.class, args);
	}

	private static void loadEnvVariables() {
		String envPath = ".env";
		if (Files.exists(Paths.get(envPath))) {
			try (Stream<String> lines = Files.lines(Paths.get(envPath))) {
				lines.filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
						.forEach(line -> {
							String[] parts = line.split("=", 2);
							if (parts.length == 2) {
								String key = parts[0].trim();
								String value = parts[1].trim();
								// Remove quotes if present
								if (value.startsWith("\"") && value.endsWith("\"")) {
									value = value.substring(1, value.length() - 1);
								}
								System.setProperty(key, value);
							}
						});
				System.out.println("Loaded environment variables from .env file");
			} catch (IOException e) {
				System.err.println("Failed to load .env file: " + e.getMessage());
			}
		}
	}

}
