package com.example.carsharingapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarSharingAppApplication {
    public static void main(String[] args) {
        String activeProfile = System.getProperty(
                "spring.profiles.active", System.getenv("SPRING_PROFILES_ACTIVE")
        );
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = "dev";
        }
        if ("dev".equals(activeProfile)) {
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        }
        SpringApplication.run(CarSharingAppApplication.class, args);
    }
}
