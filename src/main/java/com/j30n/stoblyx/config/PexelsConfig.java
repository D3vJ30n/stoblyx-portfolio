package com.j30n.stoblyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PexelsConfig {
    
    public static final String API_KEY = "Yyicakz4WGEu9XntPLqVIR4JUKEAokSMG7FfoAc35m6kHhnJnu5kHkPa";
    public static final String BASE_URL = "https://api.pexels.com/v1";
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
