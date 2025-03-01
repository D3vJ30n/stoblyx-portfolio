package com.j30n.stoblyx.config;

import com.j30n.stoblyx.config.ArgumentTestResolvers.CurrentUserArgumentTestResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 테스트 컨텍스트를 위한 공통 설정
 * 테스트 환경에서 필요한 기본 설정을 제공합니다.
 */
@TestConfiguration
@EnableSpringDataWebSupport
@ActiveProfiles("test")
public class ContextTestConfig implements WebMvcConfigurer {

    /**
     * 테스트 환경 활성화 여부 (true로 설정하면 실제 외부 API 호출을 모킹으로 대체)
     */
    @Bean
    @Primary
    public boolean testModeEnabled() {
        return true;
    }

    /**
     * 테스트에서 사용할 CurrentUser 어노테이션을 처리하는 ArgumentResolver 빈
     */
    @Bean
    public HandlerMethodArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentTestResolver();
    }
    
    /**
     * ArgumentResolver를 등록합니다.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        HandlerMethodArgumentResolver resolver = currentUserArgumentResolver();
        resolvers.add(resolver);
        System.out.println("ContextTestConfig: CurrentUserArgumentTestResolver가 등록되었습니다. 등록된 리졸버: " + resolver.getClass().getName());
    }
} 