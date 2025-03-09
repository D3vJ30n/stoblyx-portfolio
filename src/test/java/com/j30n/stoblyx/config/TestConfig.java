package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 테스트 환경에서 사용할 설정 클래스
 * 테스트 컨트롤러와 모킹 설정을 명시적으로 스캔합니다.
 */
@TestConfiguration
@ComponentScan(basePackages = {
    "com.j30n.stoblyx.config"  // TestController가 있는 패키지
})
@Import({MockTestConfig.class, XssExclusionTestConfig.class, SecurityTestConfig.class})
public class TestConfig {
    // 추가 설정이 필요하면 여기에 작성
} 