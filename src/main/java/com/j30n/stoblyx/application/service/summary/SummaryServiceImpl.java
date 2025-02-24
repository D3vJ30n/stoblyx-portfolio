package com.j30n.stoblyx.application.service.summary;

import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Summary;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final BookRepository bookRepository;
    private final SummaryRepository summaryRepository;

    @Override
    @Transactional
    public SummaryResponse createSummary(Long bookId, SummaryRequest request) {
        log.debug("책 요약 생성: bookId={}, request={}", bookId, request);
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        Summary summary = Summary.builder()
            .book(book)
            .content(request.content())
            .chapter(request.chapter())
            .page(request.page())
            .build();

        summaryRepository.save(summary);
        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryResponse getSummary(Long summaryId) {
        log.debug("책 요약 조회: summaryId={}", summaryId);
        
        Summary summary = summaryRepository.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SummaryResponse> getSummaries(Long bookId, Pageable pageable) {
        log.debug("책 요약 목록 조회: bookId={}, pageable={}", bookId, pageable);
        
        return summaryRepository.findByBookIdAndDeletedFalse(bookId, pageable)
            .map(SummaryResponse::from);
    }

    @Override
    @Transactional
    public SummaryResponse updateSummary(Long summaryId, SummaryRequest request) {
        log.debug("책 요약 수정: summaryId={}, request={}", summaryId, request);
        
        Summary summary = summaryRepository.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        summary.update(request.content(), request.chapter(), request.page());
        return SummaryResponse.from(summary);
    }

    @Override
    @Transactional
    public void deleteSummary(Long summaryId) {
        log.debug("책 요약 삭제: summaryId={}", summaryId);
        
        Summary summary = summaryRepository.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요약입니다."));

        summary.delete();
    }
} 