package com.j30n.stoblyx.config;

import com.j30n.stoblyx.infrastructure.config.XssExclusionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 환경에서 사용할 XSS 필터 제외 설정
 */
@Configuration
@Profile("test")
public class XssExclusionTestConfig {

    /**
     * 테스트용 XssExclusionConfig 빈을 생성합니다.
     * 
     * @return XssExclusionConfig 테스트용 인스턴스
     */
    @Bean
    @Primary
    public XssExclusionConfig xssExclusionConfig() {
        XssExclusionConfig config = new XssExclusionConfig();
        List<String> exclusions = new ArrayList<>();
        // 테스트에서 사용할 제외 패턴 추가
        exclusions.add("/api/health.*");
        exclusions.add("/h2-console.*");
        exclusions.add("/books.*");
        exclusions.add("/quotes.*");
        exclusions.add("/test/quotes.*");
        config.setExclusions(exclusions);
        return config;
    }
} 