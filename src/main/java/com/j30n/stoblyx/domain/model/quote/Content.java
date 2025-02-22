package com.j30n.stoblyx.domain.model.quote;

import lombok.Value;

/**
 * 인용구 내용 값 객체
 */
@Value
public class Content {
    String value;

    public Content(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("인용구 내용은 비어있을 수 없습니다");
        }
        if (value.length() > 5000) {
            throw new IllegalArgumentException("인용구 내용은 5000자를 초과할 수 없습니다");
        }
        this.value = value.trim();
    }
} 