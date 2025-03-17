package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ApiResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.port.in.book.BookContentQuoteSummaryUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 책 내용 검색과 인용구 요약을 연결하는 컨트롤러
 * 키워드로 책 내용을 검색하고 해당 내용을 기반으로 인용구를 생성 및 저장하는 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookContentQuoteSummaryController {

    private final BookContentQuoteSummaryUseCase bookContentQuoteSummaryUseCase;

    /**
     * 책에서 키워드 관련 섹션을 찾아 인용구로 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxLength 최대 섹션 길이
     * @return 요약된 인용구 응답
     */
    @GetMapping("/{bookId}/keyword-summary")
    public ResponseEntity<ApiResponse<QuoteSummaryResponse>> findAndCreateQuoteSummary(
            @PathVariable Long bookId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int maxLength) {
        
        try {
            log.debug("키워드로 책 내용 검색 및 요약 요청: bookId={}, keyword={}, maxLength={}", 
                    bookId, keyword, maxLength);
            
            QuoteSummaryResponse response = bookContentQuoteSummaryUseCase.findAndCreateQuoteSummary(
                    bookId, keyword, maxLength);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "키워드로 책 내용 검색 및 요약 성공", response));
            
        } catch (Exception e) {
            log.error("키워드로 책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "키워드로 책 내용 검색 및 요약 중 오류 발생: " + e.getMessage(), null));
        }
    }
    
    /**
     * 책에서 키워드 관련 여러 섹션을 찾아 각각 인용구로 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxLength 최대 섹션 길이
     * @param maxSections 최대 섹션 수
     * @return 요약된 인용구 응답 목록
     */
    @GetMapping("/{bookId}/keyword-summaries")
    public ResponseEntity<ApiResponse<List<QuoteSummaryResponse>>> findAndCreateMultipleQuoteSummaries(
            @PathVariable Long bookId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int maxLength,
            @RequestParam(defaultValue = "3") int maxSections) {
        
        try {
            log.debug("키워드로 여러 책 내용 검색 및 요약 요청: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                    bookId, keyword, maxLength, maxSections);
            
            List<QuoteSummaryResponse> responses = bookContentQuoteSummaryUseCase.findAndCreateMultipleQuoteSummaries(
                    bookId, keyword, maxLength, maxSections);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "키워드로 여러 책 내용 검색 및 요약 성공", responses));
            
        } catch (Exception e) {
            log.error("키워드로 여러 책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "키워드로 여러 책 내용 검색 및 요약 중 오류 발생: " + e.getMessage(), null));
        }
    }
    
    /**
     * 책에서 키워드 관련 여러 섹션을 찾아 하나의 통합된 인용구로 요약합니다.
     *
     * @param bookId 책 ID
     * @param keyword 검색어
     * @param maxLength 최대 섹션 길이
     * @param maxSections 최대 섹션 수
     * @return 통합 요약된 인용구 응답
     */
    @GetMapping("/{bookId}/integrated-keyword-summary")
    public ResponseEntity<ApiResponse<QuoteSummaryResponse>> findAndCreateIntegratedQuoteSummary(
            @PathVariable Long bookId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int maxLength,
            @RequestParam(defaultValue = "3") int maxSections) {
        
        try {
            log.debug("키워드로 통합 책 내용 검색 및 요약 요청: bookId={}, keyword={}, maxLength={}, maxSections={}", 
                    bookId, keyword, maxLength, maxSections);
            
            QuoteSummaryResponse response = bookContentQuoteSummaryUseCase.findAndCreateIntegratedQuoteSummary(
                    bookId, keyword, maxLength, maxSections);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "키워드로 통합 책 내용 검색 및 요약 성공", response));
            
        } catch (Exception e) {
            log.error("키워드로 통합 책 내용 검색 및 요약 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "키워드로 통합 책 내용 검색 및 요약 중 오류 발생: " + e.getMessage(), null));
        }
    }
    
    /**
     * 책에서 키워드 관련 섹션을 찾아 인용구로 저장합니다.
     *
     * @param bookId 책 ID
     * @param userId 사용자 ID
     * @param keyword 검색어
     * @param maxLength 최대 섹션 길이
     * @param request HTTP 요청
     * @return 저장된 인용구 ID
     */
    @PostMapping("/{bookId}/keyword-quote")
    public ResponseEntity<ApiResponse<Long>> findAndSaveQuote(
            @PathVariable Long bookId,
            @RequestParam(required = false) Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int maxLength,
            HttpServletRequest request) {
        
        try {
            // 사용자 ID가 없는 경우 요청에서 가져오기
            Long actualUserId = userId != null ? userId : getUserIdFromRequest(request);
            
            log.debug("키워드로 책 내용 검색 및 인용구 저장 요청: bookId={}, userId={}, keyword={}, maxLength={}", 
                    bookId, actualUserId, keyword, maxLength);
            
            Long quoteId = bookContentQuoteSummaryUseCase.findAndSaveQuote(
                    bookId, actualUserId, keyword, maxLength);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "키워드로 책 내용 검색 및 인용구 저장 성공", quoteId));
            
        } catch (Exception e) {
            log.error("키워드로 책 내용 검색 및 인용구 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "키워드로 책 내용 검색 및 인용구 저장 중 오류 발생: " + e.getMessage(), null));
        }
    }
    
    /**
     * 책에서 키워드 관련 여러 섹션을 찾아 각각 인용구로 저장합니다.
     *
     * @param bookId 책 ID
     * @param userId 사용자 ID
     * @param keyword 검색어
     * @param maxLength 최대 섹션 길이
     * @param maxSections 최대 섹션 수
     * @param request HTTP 요청
     * @return 저장된 인용구 ID 목록
     */
    @PostMapping("/{bookId}/keyword-quotes")
    public ResponseEntity<ApiResponse<List<Long>>> findAndSaveMultipleQuotes(
            @PathVariable Long bookId,
            @RequestParam(required = false) Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int maxLength,
            @RequestParam(defaultValue = "3") int maxSections,
            HttpServletRequest request) {
        
        try {
            // 사용자 ID가 없는 경우 요청에서 가져오기
            Long actualUserId = userId != null ? userId : getUserIdFromRequest(request);
            
            log.debug("키워드로 여러 책 내용 검색 및 인용구 저장 요청: bookId={}, userId={}, keyword={}, maxLength={}, maxSections={}", 
                    bookId, actualUserId, keyword, maxLength, maxSections);
            
            List<Long> quoteIds = bookContentQuoteSummaryUseCase.findAndSaveMultipleQuotes(
                    bookId, actualUserId, keyword, maxLength, maxSections);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "키워드로 여러 책 내용 검색 및 인용구 저장 성공", quoteIds));
            
        } catch (Exception e) {
            log.error("키워드로 여러 책 내용 검색 및 인용구 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "키워드로 여러 책 내용 검색 및 인용구 저장 중 오류 발생: " + e.getMessage(), null));
        }
    }
    
    /**
     * HTTP 요청에서 사용자 ID를 추출합니다.
     * 실제 구현에서는 인증 정보에서 가져와야 합니다.
     *
     * @param request HTTP 요청
     * @return 사용자 ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 기본값으로 1L 반환 (실제 구현에서는 인증 정보에서 가져와야 함)
        return 1L;
    }
} 