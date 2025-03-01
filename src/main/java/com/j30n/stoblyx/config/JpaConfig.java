package com.j30n.stoblyx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 관련 설정을 담당하는 클래스
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA 관련 추가 설정이 필요한 경우 이곳에 구현
}
