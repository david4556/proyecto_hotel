package com.davidpao.reservaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (scanBasePackages = {"com.davidpao.reservaciones", "com.davidpao.commons"})
public class ReservacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservacionesApplication.class, args);
	}

}
