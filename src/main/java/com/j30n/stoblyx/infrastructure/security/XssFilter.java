package com.j30n.stoblyx.infrastructure.security;

import com.j30n.stoblyx.infrastructure.config.XssExclusionConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * XSS 공격 방지를 위한 필터
 * 모든 HTTP 요청에 XssRequestWrapper를 적용하여 XSS 공격을 방지합니다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter implements Filter {

    @Autowired
    private XssExclusionConfig xssExclusionConfig;

    /**
     * XSS 필터 초기화
     *
     * @param filterConfig 필터 설정
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // 초기화 로직 없음
    }

    /**
     * 모든 HTTP 요청에 XssRequestWrapper를 적용합니다.
     * 단, XssExclusionConfig에 설정된 URL 패턴에 해당하는 요청은 제외합니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param chain    필터 체인
     * @throws IOException      입출력 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // HTTP 요청인 경우에만 XssRequestWrapper 적용
        if (request instanceof HttpServletRequest httpRequest) {
            // 요청 URL 가져오기
            String requestURI = httpRequest.getRequestURI();
            
            // XSS 필터를 적용하지 않을 URL 패턴에 해당하는 경우 필터를 적용하지 않음
            if (xssExclusionConfig.isExcluded(requestURI)) {
                chain.doFilter(request, response);
                return;
            }
            
            // XSS 필터 적용
            XssRequestWrapper xssRequestWrapper = new XssRequestWrapper(httpRequest);
            chain.doFilter(xssRequestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * XSS 필터 종료
     */
    @Override
    public void destroy() {
        // 종료 로직 없음
    }
} 