package com.JhonCodari.GestorFacil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestorFacilApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorFacilApplication.class, args);
	}

}
