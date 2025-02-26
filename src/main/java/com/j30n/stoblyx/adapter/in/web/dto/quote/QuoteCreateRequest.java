package com.j30n.stoblyx.adapter.in.web.dto.quote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuoteCreateRequest(
    @NotNull(message = "책 ID는 필수입니다.")
    Long bookId,
    
    @NotBlank(message = "내용은 필수입니다.")
    String content,
    
    String memo,
    
    @NotNull(message = "페이지 번호는 필수입니다.")
    Integer page
) {
    public QuoteCreateRequest {
        if (content != null) content = content.trim();
        if (memo != null) memo = memo.trim();
    }
} 