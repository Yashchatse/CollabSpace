package com.collabspace.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CollabspaceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollabspaceAuthApplication.class, args);
    }
}