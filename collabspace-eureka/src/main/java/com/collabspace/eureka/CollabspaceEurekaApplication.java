package com.collabspace.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CollabspaceEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollabspaceEurekaApplication.class, args);
    }
}