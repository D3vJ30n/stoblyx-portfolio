package com.j30n.stoblyx.application.service.summary;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SummaryService {
    SummaryResponse createSummary(Long bookId, SummaryRequest request);
    SummaryResponse getSummary(Long summaryId);
    Page<SummaryResponse> getSummaries(Long bookId, Pageable pageable);
    SummaryResponse updateSummary(Long summaryId, SummaryRequest request);
    void deleteSummary(Long summaryId);
} 