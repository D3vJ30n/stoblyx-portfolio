package com.j30n.stoblyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class PexelsConfig {
    
    public static final String API_KEY = "Yyicakz4WGEu9XntPLqVIR4JUKEAokSMG7FfoAc35m6kHhnJnu5kHkPa";
    public static final String BASE_URL = "https://api.pexels.com/v1";
    public static final String VIDEO_BASE_URL = "https://api.pexels.com/videos";
    public static final String IMAGE_BASE_URL = "https://api.pexels.com/v1/search";
    
    @Bean
    public RestTemplate pexelsRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // 요청마다 Authorization 헤더를 자동으로 추가하는 Interceptor 등록
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, API_KEY);
            return execution.execute(request, body);
        };
        
        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
    }
}
