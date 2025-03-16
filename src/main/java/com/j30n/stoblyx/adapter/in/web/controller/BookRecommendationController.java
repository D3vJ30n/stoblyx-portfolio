package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookRecommendationResponse;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.port.in.recommendation.ContentBasedRecommendationUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 도서 추천 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/books/recommendations")
@RequiredArgsConstructor
public class BookRecommendationController {

    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final BookRepository bookRepository;
    private final ContentBasedRecommendationUseCase contentBasedRecommendationUseCase;

    /**
     * 사용자 유사성 기반 도서 추천 목록 조회 API
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param size          한 번에 조회할 도서 수량
     * @return 추천 도서 목록
     */
    @GetMapping("/similarity")
    public ResponseEntity<ApiResponse<List<BookRecommendationResponse>>> getBookRecommendationsBySimilarity(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 도서 추천 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 유사성 기반 도서 추천 요청: userId={}, size={}", userPrincipal.getId(), size);

            // 사용자 유사도 기반으로 추천 목록 조회
            Pageable pageable = PageRequest.of(0, size, Sort.by("similarityScore").descending());
            // 간단한 구현: 인기 도서를 추천
            Page<Book> recommendedBooks = bookRepository.findMostPopularBooks(pageable);

            List<BookRecommendationResponse> bookResponses = recommendedBooks.getContent().stream()
                .map(book -> BookRecommendationResponse.fromEntity(
                    book,
                    calculateSimilarityScore(book),
                    "사용자 취향과 유사한 도서"
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "사용자 유사성 기반 도서 추천 목록입니다.", bookResponses)
            );
        } catch (Exception e) {
            log.error("도서 추천 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "도서 추천 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 오늘의 추천 도서 조회 API
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 오늘의 추천 도서
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<BookRecommendationResponse>> getDailyBookRecommendation(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 오늘의 추천 도서 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("오늘의 추천 도서 요청: userId={}", userPrincipal.getId());

            // 오늘의 추천 도서 선정 (간단한 구현: 가장 인기 있는 도서 추천)
            Pageable pageable = PageRequest.of(0, 1, Sort.by("popularity").descending());
            Page<Book> popularBooks = bookRepository.findMostPopularBooks(pageable);

            if (popularBooks.isEmpty()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(RESULT_SUCCESS, "추천 도서가 없습니다.", null)
                );
            }

            Book recommendedBook = popularBooks.getContent().get(0);
            BookRecommendationResponse bookResponse = BookRecommendationResponse.fromEntity(
                recommendedBook,
                1.0, // 최고 추천 점수
                "오늘의 추천 도서"
            );

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "오늘의 추천 도서입니다.", bookResponse)
            );
        } catch (Exception e) {
            log.error("오늘의 추천 도서 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "오늘의 추천 도서 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 장르별 추천 도서 목록 조회 API
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param genre         도서 장르
     * @param size          한 번에 조회할 도서 수량
     * @return 장르별 추천 도서 목록
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<BookRecommendationResponse>>> getBookRecommendationsByGenre(
        @CurrentUser UserPrincipal userPrincipal,
        @PathVariable String genre,
        @RequestParam(defaultValue = "5") int size
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 장르별 도서 추천 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("장르별 도서 추천 요청: userId={}, genre={}, size={}", userPrincipal.getId(), genre, size);

            // 장르별 인기 도서 조회
            Pageable pageable = PageRequest.of(0, size, Sort.by("popularity").descending());
            Page<Book> recommendedBooks = bookRepository.findByGenre(genre, pageable);

            List<BookRecommendationResponse> bookResponses = recommendedBooks.getContent().stream()
                .map(book -> BookRecommendationResponse.fromEntity(
                    book,
                    calculateSimilarityScore(book),
                    genre + " 장르 추천 도서"
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, genre + " 장르의 추천 도서 목록입니다.", bookResponses)
            );
        } catch (Exception e) {
            log.error("장르별 도서 추천 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "장르별 도서 추천 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 유사 도서 추천 목록 조회 API
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param size          한 번에 조회할 도서 수량
     * @return 유사 도서 목록
     */
    @GetMapping("/similar")
    public ResponseEntity<ApiResponse<List<BookRecommendationResponse>>> getSimilarBookRecommendations(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam(defaultValue = "5") int size
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 유사 도서 추천 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("유사 도서 추천 요청: userId={}, size={}", userPrincipal.getId(), size);

            // 개인화된 추천 도서 목록 조회 (새로 구현된 컨텐츠 기반 추천 시스템 사용)
            Pageable pageable = PageRequest.of(0, size);
            Page<BookResponse> recommendedBooks = contentBasedRecommendationUseCase.getPersonalizedBookRecommendations(pageable);

            if (recommendedBooks.isEmpty()) {
                // 추천 도서가 없는 경우 인기 도서 추천
                return getBookRecommendationsBySimilarity(userPrincipal, size);
            }

            List<BookRecommendationResponse> bookResponses = recommendedBooks.getContent().stream()
                .map(bookResponse -> {
                    Book book = bookRepository.findById(bookResponse.id())
                        .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없습니다: " + bookResponse.id()));
                    return BookRecommendationResponse.fromEntity(
                        book,
                        calculateSimilarityScore(book),
                        "사용자 관심사 기반 추천 도서"
                    );
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "유사 도서 추천 목록입니다.", bookResponses)
            );
        } catch (Exception e) {
            log.error("유사 도서 추천 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "유사 도서 추천 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 도서와 사용자 취향 간의 유사도 점수 계산
     *
     * @param book 도서 정보
     * @return 유사도 점수 (0.0 ~ 1.0)
     */
    private Double calculateSimilarityScore(Book book) {
        // 실제 구현에서는 사용자의 취향과 도서 간의 유사도를 계산
        // 여기서는 인기도를 기반으로 간단히 유사도 점수 계산
        int popularity = book.getPopularity() != null ? book.getPopularity() : 0;
        return Math.min(1.0, popularity / 100.0);
    }
} 