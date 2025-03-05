package com.j30n.stoblyx.common.util;

import jakarta.servlet.*;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

/**
 * Lucy-XSS-Filter 대신 Spring의 HtmlUtils를 사용하는 XSS 필터 구현
 */
public class XssFilterAdapter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        // XSS 필터링 로직을 직접 구현
        XssRequestWrapper wrappedRequest = new XssRequestWrapper((jakarta.servlet.http.HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 필요 없음
    }
    
    @Override
    public void destroy() {
        // 정리 필요 없음
    }
    
    /**
     * XSS 공격을 방지하기 위한 요청 래퍼 클래스
     */
    private static class XssRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        
        public XssRequestWrapper(jakarta.servlet.http.HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return value != null ? HtmlUtils.htmlEscape(value) : null;
        }
        
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return new String[0];
            }
            
            String[] escapedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                escapedValues[i] = values[i] != null ? HtmlUtils.htmlEscape(values[i]) : null;
            }
            return escapedValues;
        }
    }
} 