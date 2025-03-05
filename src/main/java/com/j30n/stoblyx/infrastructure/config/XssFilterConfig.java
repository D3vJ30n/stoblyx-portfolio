package com.j30n.stoblyx.infrastructure.config;

import com.j30n.stoblyx.infrastructure.security.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XSS 필터 설정을 위한 구성 클래스
 */
@Configuration
public class XssFilterConfig {

    /**
     * XSS 필터를 등록합니다.
     *
     * @param xssFilter XSS 필터
     * @return 필터 등록 빈
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssFilter xssFilter) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(xssFilter);
        registration.addUrlPatterns("/*"); // 모든 URL에 적용
        registration.setName("xssFilter");
        registration.setOrder(1); // 필터 순서 설정
        return registration;
    }
} 