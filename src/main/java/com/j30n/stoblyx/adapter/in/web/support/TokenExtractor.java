package com.j30n.stoblyx.adapter.in.web.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * HTTP Authorization 헤더에서 토큰을 추출하는 유틸리티 클래스
 */
@Component
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Bearer 토큰 형식의 문자열에서 실제 토큰 값만 추출합니다.
     *
     * @param bearerToken "Bearer {token}" 형식의 문자열
     * @return 추출된 토큰 값
     * @throws IllegalArgumentException 토큰 형식이 유효하지 않은 경우
     */
    public String extractToken(String bearerToken) {
        if (bearerToken == null) {
            throw new IllegalArgumentException("토큰이 null입니다.");
        }
        
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다. 'Bearer ' 접두사가 필요합니다.");
        }
        
        String token = bearerToken.substring(BEARER_PREFIX.length());
        
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("토큰 값이 비어있습니다.");
        }
        
        return token;
    }
}