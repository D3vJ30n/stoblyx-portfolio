package com.j30n.stoblyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StoblyxApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoblyxApplication.class, args);
    }
} 