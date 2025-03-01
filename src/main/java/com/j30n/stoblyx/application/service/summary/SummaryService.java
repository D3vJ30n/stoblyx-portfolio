package com.j30n.stoblyx.application.service.summary;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.application.port.in.summary.SummaryUseCase;
import com.j30n.stoblyx.application.port.out.summary.SummaryPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Summary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService implements SummaryUseCase {

    private final SummaryPort summaryPort;

    @Override
    @Transactional
    public SummaryResponse createSummary(Long bookId, SummaryRequest request) {
        log.debug("책 요약 생성: bookId={}, request={}", bookId, request);
        
        Book book = summaryPort.findBookById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        Summary summary = Summary.builder()
            .book(book)
            .content(request.content())
            .chapter(request.chapter())
            .page(request.page())
            .build();

        summaryPort.save(summary);
        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryResponse getSummary(Long summaryId) {
        log.debug("책 요약 조회: summaryId={}", summaryId);
        
        Summary summary = summaryPort.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SummaryResponse> getSummaries(Long bookId, Pageable pageable) {
        log.debug("책 요약 목록 조회: bookId={}", bookId);
        
        Book book = summaryPort.findBookById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        return summaryPort.findByBook(book, pageable)
            .map(SummaryResponse::from);
    }

    @Override
    @Transactional
    public SummaryResponse updateSummary(Long summaryId, SummaryRequest request) {
        log.debug("책 요약 수정: summaryId={}, request={}", summaryId, request);
        
        Summary summary = summaryPort.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        summary.update(request.content(), request.chapter(), request.page());
        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional
    public void deleteSummary(Long summaryId) {
        log.debug("책 요약 삭제: summaryId={}", summaryId);
        
        Summary summary = summaryPort.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        summaryPort.delete(summary);
    }
}
