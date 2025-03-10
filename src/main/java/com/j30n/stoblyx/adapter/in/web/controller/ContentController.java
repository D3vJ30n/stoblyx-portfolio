package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkStatusResponse;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.application.service.bookmark.BookmarkService;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final BookmarkService bookmarkService;
    
    private static final String ERROR_AUTH_REQUIRED = "인증이 필요합니다.";
    private static final String ERROR_CONTENT_NOT_FOUND = "콘텐츠를 찾을 수 없습니다.";
    private static final String ERROR_SERVER = "서버 오류가 발생했습니다.";

    /**
     * 트렌딩 콘텐츠 목록을 조회합니다.
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getTrendingContents(
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("트렌딩 콘텐츠 목록입니다.",
                contentService.getTrendingContents(pageable))
        );
    }

    /**
     * 추천 콘텐츠 목록을 조회합니다.
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getRecommendedContents(
        @AuthenticationPrincipal UserPrincipal user,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ERROR_AUTH_REQUIRED));
        }
        
        return ResponseEntity.ok(
            ApiResponse.success("추천 콘텐츠 목록입니다.",
                contentService.getRecommendedContents(user.getId(), pageable))
        );
    }

    /**
     * 특정 책의 콘텐츠 목록을 조회합니다.
     */
    @GetMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getContentsByBook(
        @PathVariable Long bookId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("책 관련 콘텐츠 목록입니다.",
                contentService.getContentsByBook(bookId, pageable))
        );
    }

    /**
     * 콘텐츠를 검색합니다.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchContents(
        @RequestParam String keyword,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.success("검색 결과입니다.",
                contentService.searchContents(keyword, pageable))
        );
    }

    /**
     * 콘텐츠 상세 정보를 조회합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentResponse>> getContent(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        try {
            ContentResponse content = contentService.getContent(id);
            if (user != null) {
                contentService.incrementViewCount(id);
            }
            return ResponseEntity.ok(
                ApiResponse.success("콘텐츠 상세 정보입니다.", content)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ERROR_CONTENT_NOT_FOUND));
        }
    }

    /**
     * 콘텐츠에 좋아요를 토글합니다.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        // 인증 확인
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ERROR_AUTH_REQUIRED));
        }
        
        try {
            contentService.toggleLike(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("좋아요를 토글했습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ERROR_CONTENT_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_SERVER));
        }
    }

    /**
     * 콘텐츠를 북마크에 추가/제거합니다.
     */
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Void>> toggleBookmark(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        // 인증 확인
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ERROR_AUTH_REQUIRED));
        }
        
        try {
            contentService.toggleBookmark(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("북마크를 토글했습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ERROR_CONTENT_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_SERVER));
        }
    }

    /**
     * 콘텐츠의 북마크 상태를 확인합니다.
     */
    @GetMapping("/{id}/bookmark/status")
    public ResponseEntity<ApiResponse<BookmarkStatusResponse>> checkBookmarkStatus(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        // 인증 확인
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ERROR_AUTH_REQUIRED));
        }
        
        try {
            BookmarkStatusResponse status = bookmarkService.checkBookmarkStatus(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("북마크 상태입니다.", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_SERVER));
        }
    }

    /**
     * 문구로부터 새로운 동영상 콘텐츠를 생성합니다.
     */
    @PostMapping("/quotes/{quoteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ContentResponse>> generateContent(
        @PathVariable Long quoteId
    ) {
        try {
            // contentService를 사용하여 콘텐츠 생성 및 응답 반환
            ContentResponse contentResponse = contentService.generateContent(quoteId);
            
            return ResponseEntity.ok(
                ApiResponse.success("콘텐츠가 생성되었습니다.", contentResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("콘텐츠 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }
}