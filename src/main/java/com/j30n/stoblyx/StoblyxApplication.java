package com.j30n.stoblyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스토블릭스(Stoblyx) - 책 속 문장을 AI 기반 숏폼 콘텐츠로 변환하는 서비스
 * <p>
 * 이 애플리케이션은 헥사고날 아키텍처(Hexagonal Architecture)를 기반으로 설계되었으며,
 * 도메인 중심 설계(DDD) 원칙을 따릅니다.
 * </p>
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class StoblyxApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoblyxApplication.class, args);
    }
} 