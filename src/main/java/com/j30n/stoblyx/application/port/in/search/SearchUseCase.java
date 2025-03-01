package com.j30n.stoblyx.application.port.in.search;

import com.j30n.stoblyx.adapter.in.web.dto.search.SearchRequest;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchResponse;
import com.j30n.stoblyx.domain.model.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUseCase {
    /**
     * 통합 검색을 수행합니다.
     *
     * @param request 검색 요청 정보
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<SearchResponse> search(SearchRequest request, Pageable pageable);
    
    /**
     * 검색 기록을 저장합니다.
     *
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param userId 사용자 ID
     * @param resultCount 검색 결과 수
     * @return 저장된 검색 기록
     */
    Search saveSearchHistory(String keyword, String category, Long userId, Integer resultCount);
    
    /**
     * 사용자의 검색 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 검색 기록 목록
     */
    Page<Search> getUserSearchHistory(Long userId, Pageable pageable);
    
    /**
     * 검색 기록을 삭제합니다.
     *
     * @param searchId 검색 기록 ID
     */
    void deleteSearchHistory(Long searchId);
    
    /**
     * 사용자의 모든 검색 기록을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteAllUserSearchHistory(Long userId);
}
