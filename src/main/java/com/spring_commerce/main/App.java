package com.spring_commerce.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.spring_commerce")
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
