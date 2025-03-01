package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 테스트 환경에서 데이터 관련 설정을 담당하는 설정 클래스
 * H2 인메모리 데이터베이스와 테스트용 JPA 설정을 가져옵니다.
 */
@TestConfiguration
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
public class DataTestConfig {
} 