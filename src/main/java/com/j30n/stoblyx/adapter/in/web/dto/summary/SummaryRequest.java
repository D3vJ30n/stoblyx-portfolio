package com.j30n.stoblyx.adapter.in.web.dto.summary;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record SummaryRequest(
    @NotEmpty(message = "요약 내용은 필수입니다")
    @Size(max = 2000, message = "요약 내용은 2000자를 초과할 수 없습니다")
    String content,

    @Size(max = 100, message = "챕터 정보는 100자를 초과할 수 없습니다")
    String chapter,

    @Size(max = 50, message = "페이지 정보는 50자를 초과할 수 없습니다")
    String page
) {
    public SummaryRequest {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("요약 내용은 필수입니다");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("요약 내용은 2000자를 초과할 수 없습니다");
        }
        if (chapter != null && chapter.length() > 100) {
            throw new IllegalArgumentException("챕터 정보는 100자를 초과할 수 없습니다");
        }
        if (page != null && page.length() > 50) {
            throw new IllegalArgumentException("페이지 정보는 50자를 초과할 수 없습니다");
        }
    }
} 