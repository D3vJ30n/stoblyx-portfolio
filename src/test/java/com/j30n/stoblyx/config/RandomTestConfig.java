package com.j30n.stoblyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Random;

/**
 * 테스트 환경에서 사용할 Random 객체 설정
 * 고정된 시드를 사용하여 테스트의 일관성 보장
 */
@Configuration
public class RandomTestConfig {

    /**
     * 테스트용 Random 객체를 생성합니다.
     * 고정된 시드(42)를 사용하여 테스트 실행 시 항상 동일한 난수 시퀀스를 생성합니다.
     *
     * @return 고정된 시드를 가진 Random 객체
     */
    @Bean
    @Primary
    public Random random() {
        return new Random(42); // 고정된 시드 값 사용
    }
} 