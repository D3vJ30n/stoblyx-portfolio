package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.application.service.summary.SummaryService;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 책 요약 관련 API를 처리하는 컨트롤러
 * 책 요약의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 */
@Slf4j
@RestController
@RequestMapping("/books/{bookId}/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    /**
     * 새로운 책 요약을 생성합니다.
     *
     * @param bookId 책 ID
     * @param request 요약 생성 요청 DTO
     * @return 생성된 요약 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SummaryResponse>> createSummary(
        @PathVariable Long bookId,
        @Valid @RequestBody SummaryRequest request
    ) {
        try {
            SummaryResponse response = summaryService.createSummary(bookId, request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "책 요약이 성공적으로 생성되었습니다.", response));
        } catch (Exception e) {
            log.error("책 요약 생성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "책 요약 생성에 실패했습니다.", null));
        }
    }

    /**
     * ID로 책 요약을 조회합니다.
     *
     * @param bookId 책 ID
     * @param summaryId 요약 ID
     * @return 조회된 요약 정보
     */
    @GetMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(
        @PathVariable Long bookId,
        @PathVariable Long summaryId
    ) {
        try {
            SummaryResponse response = summaryService.getSummary(summaryId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "책 요약을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("책 요약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "책 요약 조회에 실패했습니다.", null));
        }
    }

    /**
     * 책의 요약 목록을 조회합니다.
     *
     * @param bookId 책 ID
     * @param pageable 페이징 정보
     * @return 요약 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SummaryResponse>>> getSummaries(
        @PathVariable Long bookId,
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        try {
            Page<SummaryResponse> response = summaryService.getSummaries(bookId, pageable);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "책 요약 목록을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("책 요약 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "책 요약 목록 조회에 실패했습니다.", null));
        }
    }

    /**
     * 책 요약을 수정합니다.
     *
     * @param bookId 책 ID
     * @param summaryId 요약 ID
     * @param request 수정할 요약 정보
     * @return 수정된 요약 정보
     */
    @PutMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<SummaryResponse>> updateSummary(
        @PathVariable Long bookId,
        @PathVariable Long summaryId,
        @Valid @RequestBody SummaryRequest request
    ) {
        try {
            SummaryResponse response = summaryService.updateSummary(summaryId, request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "책 요약이 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            log.error("책 요약 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "책 요약 수정에 실패했습니다.", null));
        }
    }

    /**
     * 책 요약을 삭제합니다.
     *
     * @param bookId 책 ID
     * @param summaryId 요약 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<Void>> deleteSummary(
        @PathVariable Long bookId,
        @PathVariable Long summaryId
    ) {
        try {
            summaryService.deleteSummary(summaryId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "책 요약이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("책 요약 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "책 요약 삭제에 실패했습니다.", null));
        }
    }
} 