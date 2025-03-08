package com.j30n.stoblyx.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * API 요청 모니터링 인터셉터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;
    private final MonitoringConfig monitoringConfig;
    
    private static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * 요청 처리 전 실행
     * 요청 시작 시간을 기록
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    /**
     * 요청 처리 후 실행
     * 요청 처리 시간을 측정하고 메트릭으로 기록
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String apiName = getApiName(request);
            
            // 메트릭 기록
            monitoringConfig.recordApiTiming(meterRegistry, apiName, duration);
            
            // 응답 시간이 1초를 초과하는 경우 로그 기록
            if (duration > 1000) {
                log.warn("Slow API call: {} took {}ms", apiName, duration);
            }
        }
    }

    /**
     * API 이름 추출
     * 
     * @param request HTTP 요청
     * @return API 이름
     */
    private String getApiName(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // ID 값 등 가변적인 부분을 제거하여 API 그룹화
        String normalizedUri = normalizeUri(uri);
        
        return method + ":" + normalizedUri;
    }

    /**
     * URI 정규화
     * 숫자로만 이루어진 경로 세그먼트를 {id}로 대체
     * 
     * @param uri 원본 URI
     * @return 정규화된 URI
     */
    private String normalizeUri(String uri) {
        // 경로 세그먼트 분리
        String[] segments = uri.split("/");
        StringBuilder normalizedUri = new StringBuilder();
        
        for (String segment : segments) {
            if (segment.isEmpty()) {
                normalizedUri.append("/");
                continue;
            }
            
            // 숫자로만 이루어진 세그먼트는 {id}로 대체
            if (segment.matches("\\d+")) {
                normalizedUri.append("{id}/");
            } else {
                normalizedUri.append(segment).append("/");
            }
        }
        
        // 마지막 슬래시 제거
        String result = normalizedUri.toString();
        if (result.endsWith("/") && result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
} 