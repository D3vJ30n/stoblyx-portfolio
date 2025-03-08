package com.j30n.stoblyx.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 모니터링 웹 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class MonitoringWebConfig implements WebMvcConfigurer {

    private final MonitoringInterceptor monitoringInterceptor;

    /**
     * 인터셉터 등록
     * 
     * @param registry 인터셉터 레지스트리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(monitoringInterceptor)
                .addPathPatterns("/**") // 모든 경로 모니터링
                .excludePathPatterns("/health/**") // 헬스 체크 제외
                .excludePathPatterns("/actuator/**"); // 액추에이터 엔드포인트 제외
    }
} 