package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.out.persistence.ai.KoBartClient;
import com.j30n.stoblyx.application.port.in.book.BookContentSearchUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.summary.SummaryPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Summary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 책 내용에서 검색어와 관련된 섹션을 찾는 서비스
 * 책 요약 및 내용을 검색어로 분석하여 가장 관련성 높은 섹션을 추출한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookContentSearchService implements BookContentSearchUseCase {

    private final BookPort bookPort;
    private final SummaryPort summaryPort;
    private final KoBartClient koBartClient;
    
    /**
     * 최대 토큰 길이 설정 (KoBART 모델의 제한인 1024보다 약간 작게 설정)
     */
    private static final int DEFAULT_MAX_TOKEN_LENGTH = 1000;
    
    /**
     * 기본 섹션 수 
     */
    private static final int DEFAULT_MAX_SECTIONS = 3;

    @Override
    @Transactional(readOnly = true)
    public String findRelevantSection(Long bookId, String keyword, int maxSectionLength) {
        // 단일 섹션만 필요한 경우 여러 섹션을 찾은 후 첫 번째만 반환
        List<String> sections = findRelevantSections(bookId, keyword, 
                maxSectionLength > 0 ? maxSectionLength : DEFAULT_MAX_TOKEN_LENGTH, 1);
        
        return sections.isEmpty() ? "" : sections.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findRelevantSections(Long bookId, String keyword, int maxSectionLength, int maxSections) {
        log.debug("책에서 관련 섹션 검색: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, keyword, maxSectionLength, maxSections);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("검색어가 비어있어 검색을 수행할 수 없습니다.");
            return Collections.emptyList();
        }
        
        // 실제 사용할 최대 토큰 길이와 섹션 수 설정
        int actualMaxLength = maxSectionLength > 0 ? maxSectionLength : DEFAULT_MAX_TOKEN_LENGTH;
        int actualMaxSections = maxSections > 0 ? maxSections : DEFAULT_MAX_SECTIONS;
        
        try {
            // 책 정보 조회
            Book book = bookPort.findBookById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. ID: " + bookId));
            
            // 먼저 책의 요약 내용에서 검색
            List<String> relevantSections = new ArrayList<>();
            relevantSections.addAll(findRelevantSummaries(book, keyword, actualMaxSections));
            
            // 필요한 섹션 수만큼 반환
            return relevantSections.stream()
                    .limit(actualMaxSections)
                    .map(section -> truncateToMaxLength(section, actualMaxLength))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("책 내용 검색 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String findAndSummarizeRelevantSection(Long bookId, String keyword, int maxSectionLength) {
        log.debug("책에서 관련 섹션을 찾고 요약: bookId={}, keyword={}, maxLength={}", 
                bookId, keyword, maxSectionLength);
                
        String section = findRelevantSection(bookId, keyword, maxSectionLength);
        
        if (section.isEmpty()) {
            log.warn("관련 섹션을 찾을 수 없어 요약을 수행할 수 없습니다.");
            return "";
        }
        
        try {
            // KoBART를 사용하여 섹션 요약
            return koBartClient.summarizeChapter(section);
        } catch (Exception e) {
            log.error("KoBART 요약 중 오류 발생: {}", e.getMessage(), e);
            return section; // 오류 발생 시 원본 반환
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAndSummarizeRelevantSections(Long bookId, String keyword, int maxSectionLength, int maxSections) {
        log.debug("책에서 관련 섹션들을 찾고 요약: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, keyword, maxSectionLength, maxSections);
                
        List<String> sections = findRelevantSections(bookId, keyword, maxSectionLength, maxSections);
        
        if (sections.isEmpty()) {
            log.warn("관련 섹션을 찾을 수 없어 요약을 수행할 수 없습니다.");
            return Collections.emptyList();
        }
        
        try {
            // KoBART를 사용하여 각 섹션 요약
            return koBartClient.summarizeChapters(sections);
        } catch (Exception e) {
            log.error("KoBART 요약 중 오류 발생: {}", e.getMessage(), e);
            return sections; // 오류 발생 시 원본 반환
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String findAndSummarizeAsQuote(Long bookId, String keyword, int maxSectionLength, int maxSections) {
        log.debug("책에서 관련 섹션을 찾고 인용구로 요약: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                bookId, keyword, maxSectionLength, maxSections);
                
        List<String> sections = findRelevantSections(bookId, keyword, maxSectionLength, maxSections);
        
        if (sections.isEmpty()) {
            log.warn("관련 섹션을 찾을 수 없어 요약을 수행할 수 없습니다.");
            return "";
        }
        
        try {
            // 섹션을 먼저 요약한 후 인용구로 다시 요약
            return koBartClient.summarizeChaptersAndQuote(sections);
        } catch (Exception e) {
            log.error("KoBART 인용구 요약 중 오류 발생: {}", e.getMessage(), e);
            // 오류 발생 시 첫 번째 섹션 반환
            return sections.get(0);
        }
    }
    
    /**
     * 책의 요약 내용에서 검색어와 관련된 요약을 찾습니다.
     */
    private List<String> findRelevantSummaries(Book book, String keyword, int maxSections) {
        log.debug("책 요약에서 검색어 관련 내용 찾기: bookId={}, keyword={}", book.getId(), keyword);
        
        // 책의 모든 요약 조회
        Pageable pageable = PageRequest.of(0, 50); // 최대 50개 요약 조회
        Page<Summary> summaries = summaryPort.findByBook(book, pageable);
        
        if (summaries.isEmpty()) {
            log.debug("책에 요약 내용이 없습니다: bookId={}", book.getId());
            return Collections.emptyList();
        }
        
        // 검색어를 기준으로 관련성 점수 계산 후 정렬
        return summaries.getContent().stream()
                .filter(summary -> summary.getContent() != null && !summary.getContent().isEmpty())
                .sorted((s1, s2) -> compareRelevance(s2.getContent(), s1.getContent(), keyword)) // 점수 높은 순
                .map(Summary::getContent)
                .limit(maxSections)
                .collect(Collectors.toList());
    }
    
    /**
     * 두 텍스트 중 검색어와 더 관련성이 높은 텍스트를 비교합니다.
     * 검색어 등장 빈도, 문맥 등을 고려하여 점수를 매깁니다.
     */
    private int compareRelevance(String text1, String text2, String keyword) {
        // 검색어를 포함하는 경우 가중치 부여
        int score1 = calculateRelevanceScore(text1, keyword);
        int score2 = calculateRelevanceScore(text2, keyword);
        
        return Integer.compare(score1, score2);
    }
    
    /**
     * 텍스트와 검색어 간의 관련성 점수를 계산합니다.
     */
    private int calculateRelevanceScore(String text, String keyword) {
        if (text == null || keyword == null) {
            return 0;
        }
        
        // 기본 검색 (대소문자 구분 없이)
        String lowerText = text.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        
        // 검색어 빈도수 계산
        int frequency = 0;
        int index = 0;
        while ((index = lowerText.indexOf(lowerKeyword, index)) != -1) {
            frequency++;
            index += lowerKeyword.length();
        }
        
        // 검색어가 제목이나 중요 위치(앞부분)에 있는지 확인
        int positionScore = lowerText.indexOf(lowerKeyword) == -1 ? 0 : 100 - Math.min(lowerText.indexOf(lowerKeyword), 100);
        
        // 종합 점수 계산 (빈도 + 위치 가중치)
        return frequency * 10 + positionScore;
    }
    
    /**
     * 텍스트를 최대 길이로 제한합니다.
     * (실제 토큰 계산은 단순화하여 평균적인 글자 수로 대체)
     */
    private String truncateToMaxLength(String text, int maxTokenLength) {
        if (text == null) {
            return "";
        }
        
        // 한국어의 경우 대략 1글자가 1.5~2 토큰에 해당한다고 가정
        // 여유를 두고 1글자 = 1토큰으로 계산
        if (text.length() <= maxTokenLength) {
            return text;
        }
        
        // 최대 길이보다 길면 적절히 잘라서 반환
        // 문장 단위로 끊어서 반환하는 것이 바람직
        int endIndex = Math.min(text.length(), maxTokenLength);
        
        // 마지막 문장이 잘리지 않도록 마지막 마침표 위치로 조정
        int lastPeriod = text.lastIndexOf(".", endIndex);
        if (lastPeriod > endIndex * 0.7) { // 최소 70% 이상의 내용을 포함하도록
            endIndex = lastPeriod + 1;
        }
        
        return text.substring(0, endIndex);
    }
} 