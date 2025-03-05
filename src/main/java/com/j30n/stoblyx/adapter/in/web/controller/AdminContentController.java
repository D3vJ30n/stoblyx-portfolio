package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자의 콘텐츠 관리 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin/contents")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminContentController {

    private final ContentService contentService;

    /**
     * 모든 콘텐츠 목록을 페이징하여 조회합니다.
     *
     * @param status   콘텐츠 상태 필터 (선택)
     * @param keyword  검색 키워드 (선택)
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getAllContents(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        try {
            Page<ContentResponse> contents;

            if (keyword != null && !keyword.isEmpty()) {
                contents = contentService.searchContents(keyword, pageable);
            } else if (status != null && !status.isEmpty()) {
                contents = contentService.getContentsByStatus(status, pageable);
            } else {
                contents = contentService.getAllContents(pageable);
            }

            return ResponseEntity.ok(ApiResponse.success("콘텐츠 목록 조회에 성공했습니다.", contents));
        } catch (Exception e) {
            log.error("콘텐츠 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 특정 콘텐츠를 ID로 조회합니다.
     *
     * @param contentId 콘텐츠 ID
     * @return 콘텐츠 정보
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<ApiResponse<ContentResponse>> getContentById(@PathVariable Long contentId) {
        try {
            ContentResponse content = contentService.getContent(contentId);
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 조회에 성공했습니다.", content));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 콘텐츠 상태를 변경합니다.
     *
     * @param contentId 콘텐츠 ID
     * @param status    변경할 상태 (PROCESSING, COMPLETED, FAILED, PUBLISHED)
     * @return 변경된 콘텐츠 정보
     */
    @PutMapping("/{contentId}/status")
    public ResponseEntity<ApiResponse<Void>> updateContentStatus(
        @PathVariable Long contentId,
        @RequestParam String status
    ) {
        try {
            contentService.updateContentStatus(contentId, status);
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 상태가 성공적으로 변경되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 상태 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 콘텐츠를 삭제합니다.
     *
     * @param contentId 콘텐츠 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{contentId}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable Long contentId) {
        try {
            contentService.deleteContent(contentId);
            return ResponseEntity.ok(ApiResponse.success("콘텐츠가 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 여러 콘텐츠의 상태를 일괄 변경합니다.
     *
     * @param contentIds 콘텐츠 ID 목록
     * @param status     변경할 상태
     * @return 변경 결과
     */
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Void>> updateContentStatusBatch(
        @RequestParam List<Long> contentIds,
        @RequestParam String status
    ) {
        try {
            for (Long contentId : contentIds) {
                contentService.updateContentStatus(contentId, status);
            }
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 상태가 일괄적으로 변경되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 상태 일괄 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 여러 콘텐츠를 일괄 삭제합니다.
     *
     * @param contentIds 콘텐츠 ID 목록
     * @return 삭제 결과
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deleteContentBatch(@RequestParam List<Long> contentIds) {
        try {
            for (Long contentId : contentIds) {
                contentService.deleteContent(contentId);
            }
            return ResponseEntity.ok(ApiResponse.success("콘텐츠가 일괄적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 일괄 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 