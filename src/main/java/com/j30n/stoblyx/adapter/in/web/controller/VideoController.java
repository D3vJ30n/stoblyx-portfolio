package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.video.VideoCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.video.VideoResponse;
import com.j30n.stoblyx.application.service.video.VideoService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 비디오 관련 API를 처리하는 컨트롤러
 * 비디오 생성, 조회, 상태 업데이트 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 * 인증된 사용자만 접근이 가능합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<ApiResponse<VideoResponse>> createVideo(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody VideoCreateRequest request
    ) {
        try {
            VideoResponse video = videoService.createVideo(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "비디오가 성공적으로 생성되었습니다.", video));
        } catch (Exception e) {
            log.error("비디오 생성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "비디오 생성에 실패했습니다.", null));
        }
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideo(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long videoId
    ) {
        try {
            VideoResponse video = videoService.getVideo(currentUser.getId(), videoId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "비디오를 성공적으로 조회했습니다.", video));
        } catch (Exception e) {
            log.error("비디오 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "비디오 조회에 실패했습니다.", null));
        }
    }

    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideoByQuoteId(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            VideoResponse video = videoService.getVideoByQuoteId(currentUser.getId(), quoteId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "비디오를 성공적으로 조회했습니다.", video));
        } catch (Exception e) {
            log.error("비디오 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "비디오 조회에 실패했습니다.", null));
        }
    }
} 