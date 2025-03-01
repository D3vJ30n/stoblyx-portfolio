package com.j30n.stoblyx.adapter.in.web.dto.quote;

public record SavedQuoteRequest(
    String note
) {
    public SavedQuoteRequest {
        if (note != null && note.length() > 500) {
            throw new IllegalArgumentException("노트는 500자를 초과할 수 없습니다");
        }
    }
} 