package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.application.port.in.summary.SummaryUseCase;
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
 */
@Slf4j
@RestController
@RequestMapping("/books/{bookId}/summaries")
@RequiredArgsConstructor
public class SummaryController {
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private final SummaryUseCase summaryUseCase;

    /**
     * 책 요약을 생성합니다.
     *
     * @param bookId 책 ID
     * @param request 요약 생성 요청
     * @return 생성된 요약 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SummaryResponse>> createSummary(
        @PathVariable Long bookId,
        @Valid @RequestBody SummaryRequest request
    ) {
        try {
            log.info("책 요약 생성 요청: bookId={}", bookId);
            SummaryResponse response = summaryUseCase.createSummary(bookId, request);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "요약이 생성되었습니다.", response)
            );
        } catch (IllegalArgumentException e) {
            log.error("책 요약 생성 실패: bookId={}, 원인={}", bookId, e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("책 요약 생성 중 서버 오류 발생: bookId={}", bookId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, "요약 생성 중 오류가 발생했습니다.", null)
            );
        }
    }

    /**
     * 특정 요약을 조회합니다.
     *
     * @param bookId 책 ID
     * @param summaryId 요약 ID
     * @return 요약 정보
     */
    @GetMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(
        @PathVariable Long bookId,
        @PathVariable Long summaryId
    ) {
        try {
            log.info("책 요약 조회 요청: bookId={}, summaryId={}", bookId, summaryId);
            SummaryResponse response = summaryUseCase.getSummary(summaryId);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "요약 조회에 성공했습니다.", response)
            );
        } catch (IllegalArgumentException e) {
            log.error("책 요약 조회 실패: bookId={}, summaryId={}, 원인={}", bookId, summaryId, e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("책 요약 조회 중 서버 오류 발생: bookId={}, summaryId={}", bookId, summaryId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, "요약 조회 중 오류가 발생했습니다.", null)
            );
        }
    }

    /**
     * 특정 책의 모든 요약을 조회합니다.
     *
     * @param bookId 책 ID
     * @param pageable 페이징 정보
     * @return 요약 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SummaryResponse>>> getSummaries(
        @PathVariable Long bookId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        try {
            log.info("책 요약 목록 조회 요청: bookId={}", bookId);
            Page<SummaryResponse> response = summaryUseCase.getSummaries(bookId, pageable);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "요약 목록 조회에 성공했습니다.", response)
            );
        } catch (IllegalArgumentException e) {
            log.error("책 요약 목록 조회 실패: bookId={}, 원인={}", bookId, e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("책 요약 목록 조회 중 서버 오류 발생: bookId={}", bookId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, "요약 목록 조회 중 오류가 발생했습니다.", null)
            );
        }
    }

    /**
     * 요약을 수정합니다.
     *
     * @param bookId 책 ID
     * @param summaryId 요약 ID
     * @param request 수정 요청
     * @return 수정된 요약 정보
     */
    @PutMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<SummaryResponse>> updateSummary(
        @PathVariable Long bookId,
        @PathVariable Long summaryId,
        @Valid @RequestBody SummaryRequest request
    ) {
        try {
            log.info("책 요약 수정 요청: bookId={}, summaryId={}", bookId, summaryId);
            SummaryResponse response = summaryUseCase.updateSummary(summaryId, request);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "요약이 수정되었습니다.", response)
            );
        } catch (IllegalArgumentException e) {
            log.error("책 요약 수정 실패: bookId={}, summaryId={}, 원인={}", bookId, summaryId, e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("책 요약 수정 중 서버 오류 발생: bookId={}, summaryId={}", bookId, summaryId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, "요약 수정 중 오류가 발생했습니다.", null)
            );
        }
    }

    /**
     * 요약을 삭제합니다.
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
            log.info("책 요약 삭제 요청: bookId={}, summaryId={}", bookId, summaryId);
            summaryUseCase.deleteSummary(summaryId);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "요약이 삭제되었습니다.", null)
            );
        } catch (IllegalArgumentException e) {
            log.error("책 요약 삭제 실패: bookId={}, summaryId={}, 원인={}", bookId, summaryId, e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("책 요약 삭제 중 서버 오류 발생: bookId={}, summaryId={}", bookId, summaryId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, "요약 삭제 중 오류가 발생했습니다.", null)
            );
        }
    }
}