package com.j30n.stoblyx.e2e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
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
    
    /**
     * 데이터베이스 초기화를 위한 ApplicationRunner
     * 스키마 초기화 전에 테이블을 삭제합니다.
     */
    @Bean
    public ApplicationRunner databaseInitializer(@Autowired JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("E2E 테스트를 위한 데이터베이스 초기화 중...");
            try {
                // 제약 조건 삭제
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
                
                // 테이블 존재 여부 확인 후 삭제
                jdbcTemplate.execute("DROP TABLE IF EXISTS popular_search_terms CASCADE");
                
                // 제약 조건 복원
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
                
                System.out.println("데이터베이스 초기화 완료");
            } catch (Exception e) {
                System.err.println("데이터베이스 초기화 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
} 