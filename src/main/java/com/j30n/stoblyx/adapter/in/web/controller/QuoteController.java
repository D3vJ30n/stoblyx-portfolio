package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import com.j30n.stoblyx.application.service.quote.QuoteService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 문구(Quote) 관련 API를 처리하는 컨트롤러
 * 문구의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 */
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    /**
     * 새로운 문구를 생성합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param request 문구 생성 요청 DTO
     * @return 생성된 문구 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> createQuote(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody QuoteCreateRequest request
    ) {
        QuoteResponse response = quoteService.createQuote(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("SUCCESS", "문구가 성공적으로 생성되었습니다.", response));
    }

    /**
     * ID로 문구를 조회합니다.
     *
     * @param id 조회할 문구의 ID
     * @return 조회된 문구 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getQuote(
        @PathVariable Long id
    ) {
        QuoteResponse response = quoteService.getQuote(id);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구를 성공적으로 조회했습니다.", response));
    }

    /**
     * 문구 목록을 필터링하여 페이징 조회합니다.
     *
     * @param userId 사용자 ID로 필터링 (선택)
     * @param bookId 책 ID로 필터링 (선택)
     * @param pageable 페이징 정보 (기본값: page=0, size=10, sort=id,desc)
     * @return 페이징된 문구 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getQuotes(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long bookId,
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<QuoteResponse> response = quoteService.getQuotes(userId, bookId, pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 문구를 수정합니다.
     * 문구 작성자만 수정할 수 있습니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param id 수정할 문구의 ID
     * @param request 수정할 문구 정보
     * @return 수정된 문구 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> updateQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id,
        @Valid @RequestBody QuoteCreateRequest request
    ) {
        QuoteResponse response = quoteService.updateQuote(currentUser.getId(), id, request);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 문구를 삭제합니다.
     * 문구 작성자만 삭제할 수 있습니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param id 삭제할 문구의 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id
    ) {
        quoteService.deleteQuote(currentUser.getId(), id);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구가 성공적으로 삭제되었습니다.", null));
    }

    /**
     * 문구를 저장합니다.
     * 
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 저장할 문구의 ID
     * @param request 저장할 문구 정보
     * @return 저장 결과
     */
    @PostMapping("/{quoteId}/save")
    public ResponseEntity<ApiResponse<QuoteResponse>> saveQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId,
        @Valid @RequestBody SavedQuoteRequest request
    ) {
        QuoteResponse response = quoteService.saveQuote(currentUser.getId(), quoteId, request);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구가 성공적으로 저장되었습니다.", response));
    }

    /**
     * 문구 저장을 취소합니다.
     * 
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 저장 취소할 문구의 ID
     * @return 저장 취소 결과
     */
    @DeleteMapping("/{quoteId}/save")
    public ResponseEntity<ApiResponse<Void>> unsaveQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        quoteService.unsaveQuote(currentUser.getId(), quoteId);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "문구 저장이 성공적으로 취소되었습니다.", null));
    }

    /**
     * 사용자가 저장한 문구 목록을 조회합니다.
     * 
     * @param currentUser 현재 인증된 사용자
     * @param pageable 페이징 정보
     * @return 저장된 문구 목록
     */
    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getSavedQuotes(
        @CurrentUser UserPrincipal currentUser,
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<QuoteResponse> response = quoteService.getSavedQuotes(currentUser.getId(), pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "저장된 문구 목록을 성공적으로 조회했습니다.", response));
    }
} 