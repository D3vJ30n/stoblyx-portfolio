package com.j30n.stoblyx.adapter.in.web.dto.quote;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuoteCreateRequest(
    @NotEmpty(message = "문구 내용은 필수입니다")
    String content,

    Integer page,

    String chapter,

    @NotNull(message = "책 ID는 필수입니다")
    Long bookId
) {
    public QuoteCreateRequest {
        if (content != null) content = content.trim();
        if (chapter != null) chapter = chapter.trim();
    }
} 