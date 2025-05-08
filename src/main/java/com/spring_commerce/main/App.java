package com.spring_commerce.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.spring_commerce")
@EntityScan(basePackages = "com.spring_commerce.model")
@EnableJpaRepositories(basePackages = "com.spring_commerce.repositories")
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
