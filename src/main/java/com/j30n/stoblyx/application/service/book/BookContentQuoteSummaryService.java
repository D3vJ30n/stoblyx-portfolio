package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.port.in.book.BookContentQuoteSummaryUseCase;
import com.j30n.stoblyx.application.port.in.book.BookContentSearchUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 책 내용 검색과 인용구 요약을 통합하는 서비스
 * 책에서 검색어 관련 내용을 찾아 요약하고 인용구로 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookContentQuoteSummaryService implements BookContentQuoteSummaryUseCase {

    private final BookContentSearchUseCase bookContentSearchUseCase;
    private final BookPort bookPort;
    private final QuotePort quotePort;
    private final UserPort userPort;

    @Override
    @Transactional(readOnly = true)
    public QuoteSummaryResponse findAndCreateQuoteSummary(Long bookId, String keyword, int maxSectionLength) {
        log.debug("책에서 검색어 관련 섹션을 찾아 인용구로 요약: bookId={}, keyword={}, maxLength={}", 
                bookId, keyword, maxSectionLength);
        
        try {
            // 책 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 책입니다. ID: " + bookId));
            
            // 키워드로 섹션을 찾고 인용구로 요약
            String content = bookContentSearchUseCase.findRelevantSection(bookId, keyword, maxSectionLength);
            
            if (content.isEmpty()) {
                log.warn("책에서 키워드 관련 섹션을 찾을 수 없습니다: bookId={}, keyword={}", bookId, keyword);
                return QuoteSummaryResponse.builder()
                        .bookTitle(book.getTitle())
                        .build();
            }
            
            // 섹션을 요약하여 인용구로 변환
            String summarizedContent = bookContentSearchUseCase.findAndSummarizeAsQuote(
                    bookId, keyword, maxSectionLength, 1);
            
            return createQuoteSummaryResponse(null, book, content, summarizedContent);
            
        } catch (Exception e) {
            log.error("책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("책 내용 검색 및 요약 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteSummaryResponse> findAndCreateMultipleQuoteSummaries(Long bookId, String keyword, 
            int maxSectionLength, int maxSections) {
        log.debug("책에서 검색어 관련 여러 섹션을 찾아 각각 인용구로 요약: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, keyword, maxSectionLength, maxSections);
        
        try {
            // 책 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 책입니다. ID: " + bookId));
            
            // 키워드로 여러 섹션 찾기
            List<String> sections = bookContentSearchUseCase.findRelevantSections(
                    bookId, keyword, maxSectionLength, maxSections);
            
            if (sections.isEmpty()) {
                log.warn("책에서 키워드 관련 섹션을 찾을 수 없습니다: bookId={}, keyword={}", bookId, keyword);
                return Collections.emptyList();
            }
            
            // 찾은 섹션들을 각각 요약
            List<String> summarizedSections = bookContentSearchUseCase.findAndSummarizeRelevantSections(
                    bookId, keyword, maxSectionLength, maxSections);
            
            // 결과를 QuoteSummaryResponse 목록으로 변환
            List<QuoteSummaryResponse> responses = new ArrayList<>();
            for (int i = 0; i < Math.min(sections.size(), summarizedSections.size()); i++) {
                responses.add(createQuoteSummaryResponse(null, book, sections.get(i), summarizedSections.get(i)));
            }
            
            return responses;
            
        } catch (Exception e) {
            log.error("여러 책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("책 내용 검색 및 요약 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteSummaryResponse findAndCreateIntegratedQuoteSummary(Long bookId, String keyword, 
            int maxSectionLength, int maxSections) {
        log.debug("책에서 검색어 관련 여러 섹션을 찾아 통합하여 인용구로 요약: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, keyword, maxSectionLength, maxSections);
        
        try {
            // 책 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 책입니다. ID: " + bookId));
            
            // 키워드로 여러 섹션 찾기
            List<String> sections = bookContentSearchUseCase.findRelevantSections(
                    bookId, keyword, maxSectionLength, maxSections);
            
            if (sections.isEmpty()) {
                log.warn("책에서 키워드 관련 섹션을 찾을 수 없습니다: bookId={}, keyword={}", bookId, keyword);
                return QuoteSummaryResponse.builder()
                        .bookTitle(book.getTitle())
                        .build();
            }
            
            // 모든 섹션 결합하여 원본 내용 생성
            String originalContent = String.join("\n\n", sections);
            
            // 섹션을 통합하여 인용구로 요약
            String summarizedContent = bookContentSearchUseCase.findAndSummarizeAsQuote(
                    bookId, keyword, maxSectionLength, maxSections);
            
            return createQuoteSummaryResponse(null, book, originalContent, summarizedContent);
            
        } catch (Exception e) {
            log.error("통합 책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("책 내용 검색 및 요약 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public Long findAndSaveQuote(Long bookId, Long userId, String keyword, int maxSectionLength) {
        log.debug("책에서 검색어 관련 섹션을 찾아 인용구로 저장: bookId={}, userId={}, keyword={}, maxLength={}", 
                bookId, userId, keyword, maxSectionLength);
        
        try {
            // 책 정보와 사용자 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 책입니다. ID: " + bookId));
            
            User user = userPort.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. ID: " + userId));
            
            // 키워드로 섹션을 찾고 인용구로 요약
            String content = bookContentSearchUseCase.findRelevantSection(bookId, keyword, maxSectionLength);
            
            if (content.isEmpty()) {
                log.warn("책에서 키워드 관련 섹션을 찾을 수 없습니다: bookId={}, keyword={}", bookId, keyword);
                throw new IllegalArgumentException("해당 키워드와 관련된 내용을 찾을 수 없습니다: " + keyword);
            }
            
            // 인용구 저장
            Quote quote = Quote.builder()
                    .user(user)
                    .book(book)
                    .content(content)
                    .memo("키워드 \"" + keyword + "\"로 자동 생성된 인용구")
                    .page(1) // 페이지는 기본값으로 설정
                    .build();
            
            Quote savedQuote = quotePort.save(quote);
            
            return savedQuote.getId();
            
        } catch (Exception e) {
            log.error("책 내용 검색 및 인용구 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("책 내용 검색 및 인용구 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public List<Long> findAndSaveMultipleQuotes(Long bookId, Long userId, String keyword, 
            int maxSectionLength, int maxSections) {
        log.debug("책에서 검색어 관련 여러 섹션을 찾아 각각 인용구로 저장: bookId={}, userId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, userId, keyword, maxSectionLength, maxSections);
        
        try {
            // 책 정보와 사용자 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 책입니다. ID: " + bookId));
            
            User user = userPort.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. ID: " + userId));
            
            // 키워드로 여러 섹션 찾기
            List<String> sections = bookContentSearchUseCase.findRelevantSections(
                    bookId, keyword, maxSectionLength, maxSections);
            
            if (sections.isEmpty()) {
                log.warn("책에서 키워드 관련 섹션을 찾을 수 없습니다: bookId={}, keyword={}", bookId, keyword);
                return Collections.emptyList();
            }
            
            // 각 섹션을 인용구로 저장
            List<Quote> quotes = sections.stream()
                    .map(content -> Quote.builder()
                            .user(user)
                            .book(book)
                            .content(content)
                            .memo("키워드 \"" + keyword + "\"로 자동 생성된 인용구")
                            .page(1) // 페이지는 기본값으로 설정
                            .build())
                    .map(quotePort::save)
                    .collect(Collectors.toList());
            
            // 저장된 인용구 ID 목록 반환
            return quotes.stream()
                    .map(Quote::getId)
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("여러 책 내용 검색 및 인용구 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("책 내용 검색 및 인용구 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * QuoteSummaryResponse 객체를 생성합니다.
     * 
     * @param id 인용구 ID (null인 경우 임시 인용구)
     * @param book 책 정보
     * @param originalContent 원본 내용
     * @param summarizedContent 요약된 내용
     * @return 생성된 QuoteSummaryResponse 객체
     */
    private QuoteSummaryResponse createQuoteSummaryResponse(Long id, Book book, String originalContent, String summarizedContent) {
        return QuoteSummaryResponse.builder()
                .id(id)
                .originalContent(originalContent)
                .summarizedContent(summarizedContent)
                .bookTitle(book.getTitle())
                .authorNickname(null) // 임시 응답에서는 사용자 정보 불필요
                .build();
    }
} 