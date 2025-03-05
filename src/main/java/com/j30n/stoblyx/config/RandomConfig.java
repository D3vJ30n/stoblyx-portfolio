package com.j30n.stoblyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Random;

/**
 * Random 객체 설정
 * 애플리케이션 전체에서 사용할 Random 객체를 빈으로 등록합니다.
 */
@Configuration
public class RandomConfig {

    /**
     * Random 객체를 생성합니다.
     * 프로덕션 환경에서는 시드를 지정하지 않아 완전한 무작위성을 보장합니다.
     *
     * @return Random 객체
     */
    @Bean
    @Primary
    public Random random() {
        return new Random();
    }
} 