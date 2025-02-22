package com.j30n.stoblyx.domain.model.quote;

import lombok.Value;

/**
 * 인용구 페이지 값 객체
 */
@Value
public class Page {
    int value;

    public Page(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("페이지 번호는 양수여야 합니다");
        }
        this.value = value;
    }
} 