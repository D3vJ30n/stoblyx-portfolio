package com.j30n.stoblyx.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * KoBART API와 통신하는 클라이언트 클래스
 * Flask 서버에서 제공하는 텍스트 요약 API를 호출합니다.
 */
@Component
@RequiredArgsConstructor
public class KoBartClient {

    private final RestTemplate restTemplate;

    @Value("${kobart.api.url}")
    private String apiUrl;

    /**
     * 주어진 텍스트를 KoBART 모델을 사용하여 요약합니다.
     *
     * @param text 요약할 텍스트
     * @return 요약된 텍스트
     */
    public String summarize(String text) {
        ResponseEntity<SummarizeResponse> response = restTemplate.postForEntity(
            apiUrl + "/summarize",
            new SummarizeRequest(text),
            SummarizeResponse.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to get summary from KoBART API");
        }

        return response.getBody().getSummary();
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
} 