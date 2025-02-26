package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.application.port.in.summary.SummaryUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 책 요약 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/books/{bookId}/summaries")
@RequiredArgsConstructor
public class SummaryController {
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
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "요약이 생성되었습니다.",
                summaryUseCase.createSummary(bookId, request))
        );
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
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "요약 조회에 성공했습니다.",
                summaryUseCase.getSummary(summaryId))
        );
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
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "요약 목록 조회에 성공했습니다.",
                summaryUseCase.getSummaries(bookId, pageable))
        );
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
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "요약이 수정되었습니다.",
                summaryUseCase.updateSummary(summaryId, request))
        );
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
        summaryUseCase.deleteSummary(summaryId);
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "요약이 삭제되었습니다.", null)
        );
    }
}