package com.proj.slotify;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
//@EnableDiscoveryClient
@SpringBootApplication
public class SlotifyApplication {

	public static void main(String[] args) {

		// Load .env file if it exists (local development)
		// In Docker/cloud, env vars are passed directly — no .env file needed
		try {
			io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			// .env not found — using environment variables from the system
		}

		SpringApplication.run(SlotifyApplication.class, args);
	}

}
