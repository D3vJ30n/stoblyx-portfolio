package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * 테스트 환경에서 사용하는 JPA 설정 클래스
 * 테스트에서는 AuditorAware를 고정된 사용자 ID로 반환합니다.
 */
@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
public class JpaTestConfig {

    /**
     * 테스트용 AuditorAware 빈 등록
     * 테스트 환경에서는 항상 사용자 ID 1L을 반환합니다.
     *
     * @return 테스트용 AuditorAware 구현체
     */
    @Bean(name = "testAuditorAware")
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.of(1L);
    }
} 