package com.j30n.stoblyx.infrastructure.config;

import com.j30n.stoblyx.common.util.XssFilterAdapter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * XSS 방지를 위한 필터 설정 클래스
 */
@Configuration
public class XssConfig implements WebMvcConfigurer {

    /**
     * XSS 필터를 서블릿 필터로 등록합니다.
     * 모든 요청에 대해 XSS 공격을 방지하기 위한 필터링을 수행합니다.
     *
     * @return FilterRegistrationBean XSS 필터 등록 빈
     */
    @Bean
    public FilterRegistrationBean<XssFilterAdapter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilterAdapter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilterAdapter());
        registrationBean.setOrder(1);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
} 