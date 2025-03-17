package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.in.web.dto.ApiResponse;
import com.j30n.stoblyx.infrastructure.config.XssExclusionConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * WebMvcTest에서 사용할 수 있는 통합 테스트 구성 클래스
 * 모든 컨트롤러 테스트에서 공통으로 사용되는 빈과 설정을 포함합니다.
 */
@TestConfiguration
@EnableWebSecurity
public class WebMvcTestConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    @Bean
    public MonitoringConfig monitoringConfig() {
        return new MonitoringConfig();
    }
    
    @Bean
    public MonitoringInterceptor monitoringInterceptor(MeterRegistry meterRegistry, MonitoringConfig monitoringConfig) {
        return new MonitoringInterceptor(meterRegistry, monitoringConfig);
    }
    
    @Bean
    public MonitoringWebConfig monitoringWebConfig(MonitoringInterceptor monitoringInterceptor) {
        return new MonitoringWebConfig(monitoringInterceptor);
    }
    
    @Bean
    @Primary
    public XssExclusionConfig xssExclusionConfig() {
        XssExclusionConfig config = new XssExclusionConfig();
        List<String> exclusions = new ArrayList<>();
        // 모든 URL 패턴을 XSS 필터에서 제외
        exclusions.add(".*");
        config.setExclusions(exclusions);
        return config;
    }
    
    @Bean
    public ApiResponse<Object> apiResponse() {
        return new ApiResponse<>("SUCCESS", "", null);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 테스트용 설정으로 모든 보안 제약 사항을 비활성화
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
} 