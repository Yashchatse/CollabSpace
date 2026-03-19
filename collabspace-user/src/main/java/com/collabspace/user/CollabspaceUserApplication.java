package com.collabspace.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CollabspaceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabspaceUserApplication.class, args);
	}

}
