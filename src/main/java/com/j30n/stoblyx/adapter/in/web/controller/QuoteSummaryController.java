package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.service.quote.QuoteSummaryService;
import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 문구 요약 API를 처리하는 컨트롤러
 * KoBART를 이용한 문구 요약 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteSummaryController {

    private final QuoteSummaryService quoteSummaryService;

    /**
     * 특정 문구를 KoBART 모델을 사용하여 요약합니다.
     *
     * @param id 요약할 문구의 ID
     * @return 원본 문구와 요약된 문구 정보
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<QuoteSummaryResponse>> summarizeQuote(@PathVariable Long id) {
        try {
            QuoteSummaryResponse response = quoteSummaryService.summarizeQuote(id);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구가 성공적으로 요약되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 