package edu.pte.ttk.istallo_kezelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IstalloKezeloApplication {

	public static void main(String[] args) {
		SpringApplication.run(IstalloKezeloApplication.class, args);
	}

}
