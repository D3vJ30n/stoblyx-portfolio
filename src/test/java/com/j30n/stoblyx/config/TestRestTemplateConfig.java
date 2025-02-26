package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestRestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }
} 