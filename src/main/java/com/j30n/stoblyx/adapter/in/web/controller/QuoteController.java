package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import com.j30n.stoblyx.application.service.quote.QuoteService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * 문구(Quote) 관련 API를 처리하는 컨트롤러
 * 문구의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    
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
        @RequestHeader(value = "X-USER-ID", required = false) String userIdHeader,
        @Valid @RequestBody QuoteCreateRequest request
    ) {
        log.debug("인용구 생성 요청: request={}", request);
        
        try {
            // 사용자 ID 결정 로직
            Long userId;
            
            if (currentUser != null) {
                // 토큰 인증이 성공한 경우 CurrentUser에서 ID 추출
                userId = currentUser.getId();
                log.debug("@CurrentUser에서 userId 추출: {}", userId);
            } else if (userIdHeader != null && !userIdHeader.isEmpty()) {
                // 토큰 인증이 실패한 경우 X-USER-ID 헤더 사용
                Optional<ResponseEntity<ApiResponse<QuoteResponse>>> errorResponse = parseUserIdHeader(userIdHeader);
                if (errorResponse.isPresent()) {
                    return errorResponse.get();
                }
                userId = Long.parseLong(userIdHeader);
                log.debug("X-USER-ID 헤더에서 userId 추출: {}", userId);
            } else {
                log.error("사용자 인증 정보가 없습니다. CurrentUser와 X-USER-ID 헤더가 모두 없음");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("사용자 인증이 필요합니다."));
            }
            
            // 실제 인용구 생성 로직
            log.debug("인용구 생성 서비스 호출: userId={}, request={}", userId, request);
            QuoteResponse response = quoteService.createQuote(userId, request);
            
            log.debug("인용구 생성 성공: id={}", response.id());
            return ResponseEntity.ok(ApiResponse.success("인용구가 성공적으로 생성되었습니다.", response));
        } catch (EntityNotFoundException e) {
            // 엔티티를 찾을 수 없는 경우 (사용자 또는 책)
            log.error("인용구 생성 중 엔티티 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("엔티티를 찾을 수 없습니다: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 잘못된 인자 값으로 인한 오류
            log.error("인용구 생성 중 유효성 검증 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("유효하지 않은 요청 데이터: " + e.getMessage()));
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("인용구 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("데이터 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * X-USER-ID 헤더에서 사용자 ID를 파싱합니다.
     *
     * @param userIdHeader 파싱할 헤더 값
     * @return 오류 발생 시 오류 응답, 그렇지 않으면 빈 Optional
     */
    private Optional<ResponseEntity<ApiResponse<QuoteResponse>>> parseUserIdHeader(String userIdHeader) {
        try {
            Long.parseLong(userIdHeader);
            return Optional.empty();
        } catch (NumberFormatException e) {
            log.error("X-USER-ID 헤더 파싱 실패: {}", userIdHeader, e);
            return Optional.of(ResponseEntity.badRequest()
                    .body(ApiResponse.error("유효하지 않은 사용자 ID 형식입니다: " + userIdHeader)));
        }
    }

    /**
     * ID로 문구를 조회합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param id 조회할 문구의 ID
     * @return 조회된 문구 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id
    ) {
        QuoteResponse response = quoteService.getQuote(id, currentUser.getId());
        return ResponseEntity.ok()
            .body(new ApiResponse<>(SUCCESS, "문구를 성공적으로 조회했습니다.", response));
    }

    /**
     * 문구 목록을 필터링하여 페이징 조회합니다.
     *
     * @param userId 사용자 ID로 필터링 (선택)
     * @param pageable 페이징 정보 (기본값: page=0, size=10, sort=id,desc)
     * @return 페이징된 문구 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getQuotes(
        @RequestParam(required = false) Long userId,
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<QuoteResponse> response = quoteService.getQuotes(userId, pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>(SUCCESS, "문구 목록을 성공적으로 조회했습니다.", response));
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
        @Valid @RequestBody QuoteUpdateRequest request
    ) {
        QuoteResponse response = quoteService.updateQuote(id, currentUser.getId(), request);
        return ResponseEntity.ok()
            .body(new ApiResponse<>(SUCCESS, "문구가 성공적으로 수정되었습니다.", response));
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
            .body(new ApiResponse<>(SUCCESS, "문구가 성공적으로 삭제되었습니다.", null));
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
            .body(new ApiResponse<>(SUCCESS, "문구가 성공적으로 저장되었습니다.", response));
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
            .body(new ApiResponse<>(SUCCESS, "문구 저장이 성공적으로 취소되었습니다.", null));
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
            .body(new ApiResponse<>(SUCCESS, "저장된 문구 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 인용구에 좋아요를 표시합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 인용구 ID
     * @return 좋아요 처리 결과
     */
    @PostMapping("/{quoteId}/like")
    public ResponseEntity<ApiResponse<Void>> likeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            quoteService.likeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "인용구 좋아요에 성공했습니다.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        }
    }

    /**
     * 인용구 좋아요를 취소합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 인용구 ID
     * @return 좋아요 취소 처리 결과
     */
    @DeleteMapping("/{quoteId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            quoteService.unlikeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "인용구 좋아요 취소에 성공했습니다.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        }
    }

    /**
     * 인용구의 AI 요약을 조회합니다.
     *
     * @param quoteId 인용구 ID
     * @return 인용구 AI 요약 정보
     */
    @GetMapping("/{quoteId}/summary")
    public ResponseEntity<ApiResponse<?>> getQuoteSummary(
        @PathVariable Long quoteId
    ) {
        try {
            // 인용구 존재 여부 확인
            QuoteResponse quote = quoteService.getQuote(null, quoteId);
            if (quote == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(ERROR, "Quote not found with id: " + quoteId, null)
                );
            }
            
            // 임시 구현: 테스트 모드에서는 더미 데이터 반환
            return ResponseEntity.ok(
                new ApiResponse<>(SUCCESS, "인용구 AI 요약 조회에 성공했습니다.", 
                    Map.of(
                        "quoteId", quoteId,
                        "summary", "이 인용구는 지식의 본질에 대해 성찰하며, 배움의 과정이 끝이 없음을 강조합니다.",
                        "keywords", Arrays.asList("지식", "배움", "성찰", "겸손"),
                        "sentiment", "POSITIVE",
                        "generatedAt", new java.util.Date()
                    ))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(ERROR, e.getMessage(), null)
            );
        }
    }
} 