package com.j30n.stoblyx.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.mockito.Mockito;

/**
 * 테스트 환경에서 사용하는 알림 설정 클래스
 * 실제 헬스 체크 및 알림을 비활성화합니다.
 */
@TestConfiguration
@Profile("test")
public class AlertTestConfig {

    /**
     * 테스트용 가짜 HealthEndpoint 빈 생성
     * 항상 UP 상태를 반환합니다.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "testHealthEndpoint")
    public HealthEndpoint healthEndpoint() {
        HealthEndpoint mockHealthEndpoint = Mockito.mock(HealthEndpoint.class);
        org.springframework.boot.actuate.health.Health health = 
            org.springframework.boot.actuate.health.Health.up().build();
        
        Mockito.when(mockHealthEndpoint.health()).thenReturn(health);
        return mockHealthEndpoint;
    }
    
    /**
     * 테스트용 MeterRegistry 빈 생성
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "testMeterRegistry")
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
} 