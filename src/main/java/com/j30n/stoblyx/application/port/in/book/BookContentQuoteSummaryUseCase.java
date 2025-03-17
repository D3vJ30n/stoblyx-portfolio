package com.j30n.stoblyx.application.port.in.book;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;

import java.util.List;

/**
 * 책 내용 검색과 인용구 요약을 통합하는 인터페이스
 * 키워드 관련 책 내용을 찾아 요약하고, 인용구 형태로 제공합니다.
 */
public interface BookContentQuoteSummaryUseCase {
    
    /**
     * 책에서 검색어와 관련된 섹션을 찾아 인용구로 요약합니다.
     * 
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @return 요약된 인용구 응답
     */
    QuoteSummaryResponse findAndCreateQuoteSummary(Long bookId, String keyword, int maxSectionLength);
    
    /**
     * 책에서 검색어와 관련된 여러 섹션을 찾아 각각 인용구로 요약합니다.
     * 
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 요약된 인용구 응답 목록
     */
    List<QuoteSummaryResponse> findAndCreateMultipleQuoteSummaries(Long bookId, String keyword, int maxSectionLength, int maxSections);
    
    /**
     * 책에서 검색어와 관련된 여러 섹션을 찾아 하나의 통합된 인용구로 요약합니다.
     * 
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 통합 요약된 인용구 응답
     */
    QuoteSummaryResponse findAndCreateIntegratedQuoteSummary(Long bookId, String keyword, int maxSectionLength, int maxSections);
    
    /**
     * 책에서 검색어와 관련된 섹션을 찾아 인용구로 저장합니다.
     * 
     * @param bookId 책 ID
     * @param userId 사용자 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @return 저장된 인용구 ID
     */
    Long findAndSaveQuote(Long bookId, Long userId, String keyword, int maxSectionLength);
    
    /**
     * 책에서 검색어와 관련된 여러 섹션을 찾아 각각 인용구로 저장합니다.
     * 
     * @param bookId 책 ID
     * @param userId 사용자 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 저장된 인용구 ID 목록
     */
    List<Long> findAndSaveMultipleQuotes(Long bookId, Long userId, String keyword, int maxSectionLength, int maxSections);
} 