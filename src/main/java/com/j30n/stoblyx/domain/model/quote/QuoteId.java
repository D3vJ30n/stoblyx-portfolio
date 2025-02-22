package com.j30n.stoblyx.domain.model.quote;

import lombok.Value;

@Value
public class QuoteId {
    Long value;

    public QuoteId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("인용구 ID는 양수여야 합니다");
        }
        this.value = value;
    }

    public Long getValue() {  // 접근 가능한 public 메서드 추가
        return value;
    }
}
