package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkResponse;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BulkDeleteRequest;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkStatusResponse;
import com.j30n.stoblyx.application.service.bookmark.BookmarkService;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.common.response.ApiResponse;
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
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final ContentService contentService;

    /**
     * 사용자의 북마크 목록을 조회합니다.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        try {
            Page<BookmarkResponse> bookmarks = bookmarkService.getBookmarks(user.getId(), type, pageable);
            return ResponseEntity.ok(ApiResponse.success("북마크 목록입니다.", bookmarks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "북마크 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 북마크를 일괄 삭제합니다.
     */
    @PostMapping("/bulk-delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> bulkDeleteBookmarks(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody BulkDeleteRequest request
    ) {
        try {
            bookmarkService.deleteBookmarks(user.getId(), request.getContentIds());
            return ResponseEntity.ok(ApiResponse.success("북마크가 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "북마크 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 콘텐츠를 북마크에 추가/제거합니다.
     */
    @PostMapping("/content/{contentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> toggleBookmark(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long contentId
    ) {
        try {
            contentService.toggleBookmark(user.getId(), contentId);
            return ResponseEntity.ok(ApiResponse.success("북마크가 토글되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "북마크 토글 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 특정 콘텐츠의 북마크 상태를 확인합니다.
     */
    @GetMapping("/content/{contentId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookmarkStatusResponse>> checkBookmarkStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long contentId
    ) {
        try {
            BookmarkStatusResponse status = bookmarkService.checkBookmarkStatus(user.getId(), contentId);
            return ResponseEntity.ok(ApiResponse.success("북마크 상태입니다.", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "북마크 상태 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 