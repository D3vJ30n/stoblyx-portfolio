package com.j30n.stoblyx.application.port.in.summary;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SummaryUseCase {
    /**
     * 새로운 책 요약을 생성합니다.
     *
     * @param bookId 책 ID
     * @param request 요약 생성 요청
     * @return 생성된 요약 정보
     */
    SummaryResponse createSummary(Long bookId, SummaryRequest request);

    /**
     * 특정 요약을 조회합니다.
     *
     * @param summaryId 요약 ID
     * @return 요약 정보
     */
    SummaryResponse getSummary(Long summaryId);

    /**
     * 특정 책의 모든 요약을 조회합니다.
     *
     * @param bookId 책 ID
     * @param pageable 페이징 정보
     * @return 요약 목록
     */
    Page<SummaryResponse> getSummaries(Long bookId, Pageable pageable);

    /**
     * 요약을 수정합니다.
     *
     * @param summaryId 요약 ID
     * @param request 수정 요청
     * @return 수정된 요약 정보
     */
    SummaryResponse updateSummary(Long summaryId, SummaryRequest request);

    /**
     * 요약을 삭제합니다.
     *
     * @param summaryId 요약 ID
     */
    void deleteSummary(Long summaryId);
}
