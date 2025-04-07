package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentInteractionRequest;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 상호작용 관련 API를 처리하는 컨트롤러
 * 콘텐츠 상호작용 및 사용자 상호작용을 처리합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InteractionController {

    private static final String ERROR_AUTH_REQUIRED = "인증이 필요합니다.";
    private static final String ERROR_SERVER = "서버 오류가 발생했습니다.";

    private final ContentService contentService;

    /**
     * 콘텐츠 상호작용을 기록합니다.
     * 사용자가 콘텐츠와 상호작용한 내용을 기록합니다.
     */
    @PostMapping("/interactions")
    public ResponseEntity<ApiResponse<?>> recordInteraction(
        @RequestBody ContentInteractionRequest request,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ERROR_AUTH_REQUIRED));
            }

            log.info("콘텐츠 상호작용 기록: userId={}, contentId={}, interactionType={}",
                user.getId(), request.contentId(), request.interactionType());

            contentService.recordInteraction(user.getId(), request.contentId(), request.interactionType());
            return ResponseEntity.ok(
                ApiResponse.success("콘텐츠 상호작용이 성공적으로 기록되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            log.error("콘텐츠 상호작용 기록 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("콘텐츠 상호작용 기록 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ERROR_SERVER));
        }
    }

    /**
     * 사용자 상호작용을 기록합니다.
     * 사용자가 다른 사용자 또는 시스템과 상호작용한 내용을 기록합니다.
     */
    @PostMapping("/user-interactions")
    public ResponseEntity<ApiResponse<?>> recordUserInteraction(
        @RequestBody Map<String, Object> request,
        @AuthenticationPrincipal UserPrincipal user
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ERROR_AUTH_REQUIRED));
            }

            log.info("사용자 상호작용 기록: userId={}, interactionData={}", user.getId(), request);

            // 이 부분에 실제 사용자 상호작용 처리 로직을 구현합니다.
            // 현재는 단순히 성공 응답만 반환합니다.

            return ResponseEntity.ok(
                ApiResponse.success("사용자 상호작용이 성공적으로 기록되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            log.error("사용자 상호작용 기록 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("사용자 상호작용 기록 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ERROR_SERVER));
        }
    }
} 