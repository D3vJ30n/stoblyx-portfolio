package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.search.SearchHistoryResponse;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchRequest;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchResponse;
import com.j30n.stoblyx.application.port.in.search.SearchUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 검색 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    
    private final SearchUseCase searchUseCase;

    /**
     * 통합 검색 API
     * 문구와 책을 동시에 검색할 수 있습니다.
     *
     * @param request  검색 요청 DTO
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SearchResponse>>> search(
        @Valid @ModelAttribute SearchRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        try {
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "검색 결과입니다.",
                    searchUseCase.search(request, pageable))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        }
    }
    
    /**
     * 사용자의 검색 기록을 조회하는 API
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 검색 기록 목록
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<Page<SearchHistoryResponse>>> getUserSearchHistory(
        @PathVariable Long userId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<SearchHistoryResponse> searchHistories = searchUseCase.getUserSearchHistory(userId, pageable)
                .map(SearchHistoryResponse::fromEntity);
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "검색 기록 조회 결과입니다.", searchHistories)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        }
    }
    
    /**
     * 검색 기록을 삭제하는 API
     *
     * @param searchId 검색 기록 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/history/{searchId}")
    public ResponseEntity<ApiResponse<Void>> deleteSearchHistory(
        @PathVariable Long searchId
    ) {
        try {
            searchUseCase.deleteSearchHistory(searchId);
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "검색 기록이 삭제되었습니다.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        }
    }
    
    /**
     * 사용자의 모든 검색 기록을 삭제하는 API
     *
     * @param userId 사용자 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/history/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllUserSearchHistory(
        @PathVariable Long userId
    ) {
        try {
            searchUseCase.deleteAllUserSearchHistory(userId);
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "모든 검색 기록이 삭제되었습니다.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        }
    }
}
