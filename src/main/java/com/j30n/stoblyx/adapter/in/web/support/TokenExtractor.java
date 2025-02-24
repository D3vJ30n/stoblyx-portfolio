package com.j30n.stoblyx.adapter.in.web.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public String extractToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
    }
}