package com.j30n.stoblyx.adapter.in.web.dto.quote;

/**
 * 문구 요약 응답 DTO
 * 원본 문구와 요약된 문구 정보를 포함합니다.
 */
public record QuoteSummaryResponse(
    Long id,
    String originalContent,
    String summarizedContent,
    String bookTitle,
    String authorNickname
) {} 