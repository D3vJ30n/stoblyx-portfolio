package com.j30n.stoblyx.application.port.out.search;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPort {
    /**
     * 키워드와 카테고리로 문구를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 검색된 문구 목록
     */
    Page<Quote> findQuotesByKeywordAndCategory(String keyword, String category, Pageable pageable);

    /**
     * 키워드와 카테고리로 책을 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 검색된 책 목록
     */
    Page<Book> findBooksByKeywordAndCategory(String keyword, String category, Pageable pageable);
    
    /**
     * 검색 기록을 저장합니다.
     *
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param userId 사용자 ID
     * @param resultCount 검색 결과 수
     * @return 저장된 검색 기록
     */
    Search saveSearch(String keyword, String category, Long userId, Integer resultCount);
    
    /**
     * 사용자 ID로 검색 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 검색 기록 목록
     */
    Page<Search> findSearchesByUserId(Long userId, Pageable pageable);
    
    /**
     * 검색 기록을 삭제합니다.
     *
     * @param searchId 검색 기록 ID
     */
    void deleteSearch(Long searchId);
    
    /**
     * 사용자의 모든 검색 기록을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteAllSearchesByUserId(Long userId);
}
