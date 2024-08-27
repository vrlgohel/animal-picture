package com.vgohel.animalpicture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath:animal-picture.bpmn")
public class AnimalPictureApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalPictureApplication.class, args);
	}
}
