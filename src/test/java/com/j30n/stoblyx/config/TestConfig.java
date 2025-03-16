package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 테스트 전역 설정 클래스
 */
@TestConfiguration
@Configuration
@ActiveProfiles("test")
@ComponentScan(basePackages = {
    "com.j30n.stoblyx.config"  // TestController가 있는 패키지
})
@Import({MockTestConfig.class, XssExclusionTestConfig.class, SecurityTestConfig.class})
public class TestConfig {
    // 공통 테스트 설정을 여기에 추가
} 