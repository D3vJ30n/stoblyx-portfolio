package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * KoBART API와 통신하는 클라이언트 클래스
 * Flask 서버에서 제공하는 텍스트 요약 API를 호출합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KoBartClient {

    private final RestTemplate restTemplate;

    @Value("${kobart.api.url}")
    private String apiUrl;
    
    @Value("${kobart.enabled:true}")
    private boolean enabled;

    /**
     * 주어진 텍스트를 KoBART 모델을 사용하여 요약합니다.
     * KoBART가 비활성화된 경우 원본 텍스트를 그대로 반환합니다.
     *
     * @param text 요약할 텍스트
     * @return 요약된 텍스트 또는 원본 텍스트(비활성화된 경우)
     */
    public String summarize(String text) {
        if (!enabled) {
            log.info("KoBART API가 비활성화되어 있습니다. 원본 텍스트를 반환합니다.");
            return text;
        }
        
        try {
            ResponseEntity<SummarizeResponse> response = restTemplate.postForEntity(
                apiUrl + "/summarize",
                new SummarizeRequest(text),
                SummarizeResponse.class
            );

            return Optional.ofNullable(response.getBody())
                .map(SummarizeResponse::getSummary)
                .orElseThrow(() -> new KoBartException("Failed to get summary from KoBART API"));
        } catch (Exception e) {
            log.error("KoBART API 호출 중 오류 발생: {}", e.getMessage());
            // API 호출 실패 시 원본 텍스트 반환
            return text;
        }
    }

    /**
     * 요약 요청 DTO
     */
    private record SummarizeRequest(String text) {}

    /**
     * 요약 응답 DTO
     */
    private record SummarizeResponse(String summary) {
        public String getSummary() {
            return summary;
        }
    }
    
    /**
     * KoBART API 관련 예외 클래스
     */
    public static class KoBartException extends RuntimeException {
        public KoBartException(String message) {
            super(message);
        }
        
        public KoBartException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 