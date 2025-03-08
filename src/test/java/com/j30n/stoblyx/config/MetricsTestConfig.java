package com.j30n.stoblyx.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

/**
 * 테스트 환경에서 사용할 메트릭스 설정 클래스
 */
@TestConfiguration
@ActiveProfiles("test")
public class MetricsTestConfig {

    /**
     * 테스트 환경에서 사용할 MeterRegistry 빈을 생성합니다.
     * SimpleMeterRegistry를 사용하여 실제 메트릭 수집 없이 테스트를 진행할 수 있습니다.
     *
     * @return MeterRegistry 빈
     */
    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
} 