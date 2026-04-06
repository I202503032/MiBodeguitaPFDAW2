package com.proyecto.MiBodegaApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MiBodegitaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiBodegitaApplication.class, args);
	}

}
