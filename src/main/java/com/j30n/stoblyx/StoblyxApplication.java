package com.j30n.stoblyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.j30n.stoblyx")
public class StoblyxApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoblyxApplication.class, args);
    }
} 