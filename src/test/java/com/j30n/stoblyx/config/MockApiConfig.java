package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.out.persistence.ai.KoBartClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * E2E 테스트 환경에서 외부 API를 모킹하기 위한 설정 클래스
 */
@Configuration
@Profile("e2e")
public class MockApiConfig {

    /**
     * RestTemplate 빈을 제공합니다.
     * 테스트 환경에서 실제 HTTP 요청을 보내지 않도록 모킹합니다.
     *
     * @return RestTemplate 빈
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * KoBart API 클라이언트를 모킹합니다.
     * 실제 외부 API 호출 없이 테스트를 수행할 수 있도록 합니다.
     *
     * @return 모킹된 KoBartClient 빈
     */
    @Bean
    @Primary
    public KoBartClient mockKoBartClient() {
        KoBartClient mockClient = Mockito.mock(KoBartClient.class);
        
        // summarize 메서드 호출 시 모의 응답 반환
        Mockito.when(mockClient.summarize(Mockito.anyString()))
               .thenReturn("이것은 테스트를 위한 모의 요약입니다.");
        
        return mockClient;
    }
} 