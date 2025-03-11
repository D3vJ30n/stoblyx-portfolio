package com.j30n.stoblyx.e2e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import com.j30n.stoblyx.StoblyxApplication;

/**
 * E2E 테스트용 애플리케이션 테스트 클래스
 * 필요에 따라 실제 데이터베이스 대신 테스트 컨테이너 사용 가능
 */
@TestConfiguration
@ActiveProfiles("test")
public class TestApplication {
    
    public static void main(String[] args) {
        SpringApplication
            .from(StoblyxApplication::main)
            .with(TestApplication.class)
            .run(args);
    }
    
    /**
     * 테스트 초기화 메서드
     * 실제 테스트 실행시 필요한 초기 설정을 수행합니다.
     */
    @Bean
    public String testInitializer() {
        System.out.println("E2E 테스트 환경 초기화 중...");
        return "testInitializer";
    }
} 