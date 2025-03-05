package com.j30n.stoblyx.config;

import com.j30n.stoblyx.infrastructure.config.XssExclusionConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 환경에서 사용할 XSS 설정 클래스
 */
@TestConfiguration
public class XssTestConfig {

    /**
     * 테스트 환경에서 사용할 XssExclusionConfig 빈을 생성합니다.
     * 모든 URL 패턴을 XSS 필터에서 제외하도록 설정합니다.
     *
     * @return XssExclusionConfig 빈
     */
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
} 