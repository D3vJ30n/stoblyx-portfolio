package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 숏폼 콘텐츠 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/shortforms")
@RequiredArgsConstructor
public class ShortFormController {

    private final ContentService contentService;

    /**
     * 추천 숏폼 콘텐츠 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 추천 숏폼 콘텐츠 목록
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getRecommendedShortForms(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<ContentResponse> response = contentService.getRecommendedContents(pageable);
            return ResponseEntity.ok(ApiResponse.success("추천 숏폼 목록 조회에 성공했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 트렌딩 숏폼 콘텐츠 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 트렌딩 숏폼 콘텐츠 목록
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getTrendingShortForms(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<ContentResponse> response = contentService.getTrendingContents(pageable);
            return ResponseEntity.ok(ApiResponse.success("트렌딩 숏폼 목록 조회에 성공했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 특정 책과 유사한 장르의 책을 추천합니다.
     *
     * @param bookId 책 ID
     * @param pageable 페이징 정보
     * @return 유사한 장르의 책 목록
     */
    @GetMapping("/books/{bookId}/similar")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getSimilarBookShortForms(
        @PathVariable Long bookId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<ContentResponse> response = contentService.getSimilarBookContents(bookId, pageable);
            return ResponseEntity.ok(ApiResponse.success("유사한 장르의 숏폼 목록 조회에 성공했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 