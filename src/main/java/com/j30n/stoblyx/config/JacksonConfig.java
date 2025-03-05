package com.j30n.stoblyx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson ObjectMapper 설정
 * 애플리케이션 전체에서 사용할 ObjectMapper 객체를 빈으로 등록합니다.
 */
@Configuration
public class JacksonConfig {

    /**
     * ObjectMapper 객체를 생성합니다.
     * Java 8 날짜/시간 타입을 지원하도록 설정합니다.
     *
     * @return 설정된 ObjectMapper 객체
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
} 