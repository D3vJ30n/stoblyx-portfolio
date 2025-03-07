package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 루트 URL을 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class IndexController {

    /**
     * 루트 URL을 처리합니다.
     *
     * @return API 응답
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> index() {
        try {
            log.info("루트 URL 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("Stoblyx API 서버에 오신 것을 환영합니다.", "v1.0.0"));
        } catch (Exception e) {
            log.error("루트 URL 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 