package com.j30n.stoblyx.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

/**
 * 테스트용 모니터링 설정 클래스
 */
@TestConfiguration
@ActiveProfiles("test")
public class MonitoringTestConfig {

    /**
     * 테스트용 MeterRegistry 빈 제공
     * 
     * @return SimpleMeterRegistry 인스턴스
     */
    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    /**
     * 테스트용 MonitoringConfig 빈 제공
     * 
     * @return MonitoringConfig 인스턴스
     */
    @Bean
    @Primary
    public MonitoringConfig monitoringConfig() {
        return new MonitoringConfig();
    }
    
    /**
     * 테스트 환경에서 사용할 MonitoringInterceptor 빈을 생성합니다.
     * 실제 인스턴스를 생성하여 MeterRegistry와 MonitoringConfig를 주입합니다.
     *
     * @return MonitoringInterceptor 빈
     */
    @Bean
    @Primary
    public MonitoringInterceptor monitoringInterceptor(MeterRegistry meterRegistry, MonitoringConfig monitoringConfig) {
        return new MonitoringInterceptor(meterRegistry, monitoringConfig);
    }
} 