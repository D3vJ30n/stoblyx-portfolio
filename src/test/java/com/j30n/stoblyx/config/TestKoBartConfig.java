package com.j30n.stoblyx.config;

import com.j30n.stoblyx.infrastructure.client.KoBartClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Profile("test")
public class TestKoBartConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public KoBartClient koBartClient() {
        KoBartClient mockClient = Mockito.mock(KoBartClient.class);
        Mockito.when(mockClient.summarize(Mockito.anyString()))
            .thenReturn("테스트용 요약 텍스트");
        return mockClient;
    }
} 