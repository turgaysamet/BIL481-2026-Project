package com.campusbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point of the CampusBridge REST API (layered Spring Boot monolith). */
@SpringBootApplication
public class CampusBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusBridgeApplication.class, args);
    }
}
