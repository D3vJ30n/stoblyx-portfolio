package com.j30n.stoblyx.application.service.recommendation;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.port.in.recommendation.ContentBasedRecommendationUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.recommendation.RecommendationPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.SearchTermProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 콘텐츠 기반 추천 시스템 서비스
 * 검색 결과와 사용자 상호작용을 분석하여 콘텐츠를 추천합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentBasedRecommendationService implements ContentBasedRecommendationUseCase {

    private static final int MIN_KEYWORD_LENGTH = 2;
    private static final double TITLE_WEIGHT = 3.0;
    private static final double AUTHOR_WEIGHT = 2.0;
    private static final double GENRE_WEIGHT = 2.5;
    private static final double DESCRIPTION_WEIGHT = 1.0;
    private static final double INTERACTION_WEIGHT = 1.5;

    private final BookPort bookPort;
    private final RecommendationPort recommendationPort;

    /**
     * 검색 기록 기반 개인화된 도서 추천
     *
     * @param pageable 페이징 정보
     * @return 추천 도서 목록
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "personalizedBookRecommendations", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BookResponse> getPersonalizedBookRecommendations(Pageable pageable) {
        // 현재 인증된 사용자 ID 가져오기
        Long userId = recommendationPort.getCurrentUserId();
        if (userId == null) {
            log.warn("인증된 사용자 없음 - 일반 인기 도서 반환");
            return bookPort.findPopularBooks(pageable).map(BookResponse::from);
        }

        // 사용자 검색 기록 조회
        List<SearchTermProfile> searchTerms = recommendationPort.getUserSearchTerms(userId);
        if (searchTerms.isEmpty()) {
            log.info("사용자 검색 기록 없음(userId={}) - 일반 인기 도서 반환", userId);
            return bookPort.findPopularBooks(pageable).map(BookResponse::from);
        }

        // 검색어 가중치 계산
        Map<String, Double> keywordWeights = calculateKeywordWeights(searchTerms);
        log.debug("사용자 키워드 가중치(userId={}): {}", userId, keywordWeights);

        // 모든 도서 가져오기 (실제 구현에서는 효율적인 방법으로 필터링 필요)
        List<Book> allBooks = bookPort.findAllBooks();

        // 각 도서의 관련성 점수 계산
        Map<Book, Double> bookScores = calculateBookScores(allBooks, keywordWeights);

        // 점수에 따라 정렬
        List<Book> sortedBooks = bookScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedBooks.size());
        
        if (start >= sortedBooks.size()) {
            return Page.empty(pageable);
        }

        List<Book> pageContent = sortedBooks.subList(start, end);
        return new PageImpl<>(pageContent.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList()), pageable, sortedBooks.size());
    }

    /**
     * 특정 책과 유사한 책 목록 추천
     *
     * @param bookId 기준 책 ID
     * @param pageable 페이징 정보
     * @return 유사한 책 목록
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "similarBooks", key = "#bookId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BookResponse> getSimilarBooks(Long bookId, Pageable pageable) {
        // 기준 책 조회
        Book targetBook = bookPort.findBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // 기준 책의 키워드 추출
        Map<String, Double> bookKeywords = extractBookKeywords(targetBook);
        log.debug("책 키워드(bookId={}): {}", bookId, bookKeywords);

        // 모든 도서 가져오기 (실제 구현에서는 효율적인 방법으로 필터링 필요)
        List<Book> allBooks = bookPort.findAllBooks();

        // 현재 책 제외
        allBooks = allBooks.stream()
                .filter(book -> !Objects.equals(book.getId(), bookId))
                .collect(Collectors.toList());

        // 각 도서의 관련성 점수 계산
        Map<Book, Double> bookScores = calculateBookScores(allBooks, bookKeywords);

        // 점수에 따라 정렬
        List<Book> sortedBooks = bookScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedBooks.size());
        
        if (start >= sortedBooks.size()) {
            return Page.empty(pageable);
        }

        List<Book> pageContent = sortedBooks.subList(start, end);
        return new PageImpl<>(pageContent.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList()), pageable, sortedBooks.size());
    }

    /**
     * 사용자 검색어 기반 키워드 가중치 계산
     */
    private Map<String, Double> calculateKeywordWeights(List<SearchTermProfile> searchTerms) {
        Map<String, Double> keywordWeights = new HashMap<>();

        // 검색어별 가중치 계산
        for (SearchTermProfile term : searchTerms) {
            String searchTerm = term.getSearchTerm().toLowerCase();
            // 검색어를 단어로 분리
            for (String keyword : extractKeywords(searchTerm)) {
                if (keyword.length() >= MIN_KEYWORD_LENGTH) {
                    // 검색 횟수를 가중치로 사용하고, 최근성은 고려하지 않음 (SearchTermProfile에 lastSearchDate가 없음)
                    double weight = term.getSearchCount();
                    keywordWeights.merge(keyword, weight, Double::sum);
                }
            }
        }

        return keywordWeights;
    }

    /**
     * 책 정보에서 키워드 추출
     */
    private Map<String, Double> extractBookKeywords(Book book) {
        Map<String, Double> keywords = new HashMap<>();

        // 제목 키워드
        if (book.getTitle() != null) {
            for (String keyword : extractKeywords(book.getTitle())) {
                if (keyword.length() >= MIN_KEYWORD_LENGTH) {
                    keywords.merge(keyword, TITLE_WEIGHT, Double::sum);
                }
            }
        }

        // 저자 키워드
        if (book.getAuthor() != null) {
            for (String keyword : extractKeywords(book.getAuthor())) {
                if (keyword.length() >= MIN_KEYWORD_LENGTH) {
                    keywords.merge(keyword, AUTHOR_WEIGHT, Double::sum);
                }
            }
        }

        // 장르 키워드
        if (book.getGenres() != null) {
            for (String genre : book.getGenres()) {
                for (String keyword : extractKeywords(genre)) {
                    if (keyword.length() >= MIN_KEYWORD_LENGTH) {
                        keywords.merge(keyword, GENRE_WEIGHT, Double::sum);
                    }
                }
            }
        }

        // 설명 키워드
        if (book.getDescription() != null) {
            for (String keyword : extractKeywords(book.getDescription())) {
                if (keyword.length() >= MIN_KEYWORD_LENGTH) {
                    keywords.merge(keyword, DESCRIPTION_WEIGHT, Double::sum);
                }
            }
        }

        return keywords;
    }

    /**
     * 도서 관련성 점수 계산
     */
    private Map<Book, Double> calculateBookScores(List<Book> books, Map<String, Double> keywordWeights) {
        Map<Book, Double> scores = new HashMap<>();

        for (Book book : books) {
            Map<String, Double> bookKeywords = extractBookKeywords(book);
            double score = calculateSimilarityScore(keywordWeights, bookKeywords);
            
            // 인기도와 상호작용 정보를 추가로 고려할 수 있음
            double popularityBoost = book.getPopularity() != null ? book.getPopularity() * 0.1 : 0;
            // Book 클래스에 getLikesCount 메서드가 없어 인기도만 고려
            
            score += popularityBoost;
            scores.put(book, score);
        }

        return scores;
    }

    /**
     * 키워드 유사도 점수 계산
     */
    private double calculateSimilarityScore(Map<String, Double> keywordsA, Map<String, Double> keywordsB) {
        double score = 0.0;

        // 공통 키워드에 대한 점수 계산
        for (Map.Entry<String, Double> entry : keywordsA.entrySet()) {
            String keyword = entry.getKey();
            if (keywordsB.containsKey(keyword)) {
                score += entry.getValue() * keywordsB.get(keyword);
            }
        }

        return score;
    }

    /**
     * 검색어를 키워드로 분리
     */
    private List<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        // 간단한 토큰화 구현 (스페이스, 쉼표 등으로 분리)
        String[] tokens = text.toLowerCase()
                .replaceAll("[^가-힣a-z0-9\\s]", " ")
                .split("\\s+");

        // 중복 제거 및 빈 문자열 제거
        return Arrays.stream(tokens)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 최근성 가중치 계산
     */
    private double getRecencyWeight(LocalDateTime lastSearchDate) {
        if (lastSearchDate == null) {
            return 1.0;
        }

        // 현재 시간과의 차이 계산 (일 단위)
        long daysDiff = java.time.Duration.between(lastSearchDate, LocalDateTime.now()).toDays();
        
        // 최근 검색일수록 높은 가중치 부여 (최대 2배, 30일 이후 기본 가중치 1.0)
        return Math.max(1.0, 2.0 - (daysDiff / 30.0));
    }
} 