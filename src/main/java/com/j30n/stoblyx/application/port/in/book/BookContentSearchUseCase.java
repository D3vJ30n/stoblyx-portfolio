package com.j30n.stoblyx.application.port.in.book;

import java.util.List;

/**
 * 책 내용에서 검색어 관련 섹션을 찾는 Use Case
 * 책 내용을 검색어를 기준으로 분석하여 관련성 높은 섹션을 찾는다.
 */
public interface BookContentSearchUseCase {
    
    /**
     * 책 내용에서 검색어와 관련된 섹션을 찾습니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @return 검색어와 관련된 섹션 내용
     */
    String findRelevantSection(Long bookId, String keyword, int maxSectionLength);
    
    /**
     * 책 내용에서 검색어와 관련된 여러 섹션을 찾습니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 검색어와 관련된 섹션 목록
     */
    List<String> findRelevantSections(Long bookId, String keyword, int maxSectionLength, int maxSections);
    
    /**
     * 책 내용에서 검색어와 관련된 섹션을 찾고 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @return 검색어와 관련된 섹션의 요약
     */
    String findAndSummarizeRelevantSection(Long bookId, String keyword, int maxSectionLength);
    
    /**
     * 책 내용에서 검색어와 관련된 여러 섹션을 찾고 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 검색어와 관련된 각 섹션의 요약 목록
     */
    List<String> findAndSummarizeRelevantSections(Long bookId, String keyword, int maxSectionLength, int maxSections);
    
    /**
     * 책 내용에서 검색어와 관련된 여러 섹션을 찾고, 이를 하나의 인용구로 요약합니다.
     * 먼저 각 섹션을 요약한 후, 이를 결합하여 다시 인용구 형태로 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxSectionLength 섹션 최대 길이 (토큰 수 제한)
     * @param maxSections 최대 섹션 수
     * @return 검색어와 관련된 전체 내용의 인용구 형태 요약
     */
    String findAndSummarizeAsQuote(Long bookId, String keyword, int maxSectionLength, int maxSections);
} 