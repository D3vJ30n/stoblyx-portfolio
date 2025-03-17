package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .orElse(text);
        } catch (Exception e) {
            log.error("KoBART API 호출 중 오류 발생: {}", e.getMessage());
            // API 호출 실패 시 원본 텍스트 반환
            return text;
        }
    }

    /**
     * 책의 특정 챕터 내용을 요약합니다.
     * 기존 summarize 메서드를 활용하여 챕터 내용을 요약합니다.
     *
     * @param chapterContent 요약할 챕터 내용
     * @return 요약된 챕터 내용
     */
    public String summarizeChapter(String chapterContent) {
        log.info("챕터 내용 요약을 시작합니다. 텍스트 길이: {}", chapterContent.length());
        return summarize(chapterContent);
    }

    /**
     * 여러 챕터 내용을 각각 요약합니다.
     *
     * @param chapterContents 요약할 여러 챕터 내용 목록
     * @return 각 챕터 별로 요약된 내용 목록
     */
    public List<String> summarizeChapters(List<String> chapterContents) {
        log.info("여러 챕터 내용 요약을 시작합니다. 챕터 수: {}", chapterContents.size());
        return chapterContents.stream()
            .map(this::summarizeChapter)
            .collect(Collectors.toList());
    }

    /**
     * 인용구를 위한 요약을 수행합니다.
     * 이미 요약된 내용을 다시 요약하여 더 간결한 형태로 만듭니다.
     *
     * @param summaryContent 이미 요약된 내용
     * @return 더 간결하게 요약된 인용구
     */
    public String summarizeQuote(String summaryContent) {
        log.info("인용구 요약을 시작합니다. 텍스트 길이: {}", summaryContent.length());
        return summarize(summaryContent);
    }

    /**
     * 챕터 내용을 요약한 후, 추가로 인용구 형태로 요약합니다.
     * 두 단계의 요약 과정을 거쳐 매우 간결한 형태의 요약을 생성합니다.
     *
     * @param chapterContent 요약할 챕터 내용
     * @return 두 단계로 요약된 결과 (인용구 형태)
     */
    public String summarizeChapterAndQuote(String chapterContent) {
        log.info("챕터 내용 및 인용구 요약을 시작합니다. 텍스트 길이: {}", chapterContent.length());
        String chapterSummary = summarizeChapter(chapterContent);
        return summarizeQuote(chapterSummary);
    }

    /**
     * 여러 챕터 내용을 요약한 후, 모든 요약 내용을 결합하여 인용구 형태로 요약합니다.
     *
     * @param chapterContents 요약할 여러 챕터 내용 목록
     * @return 결합된 두 단계 요약 결과 (인용구 형태)
     */
    public String summarizeChaptersAndQuote(List<String> chapterContents) {
        log.info("여러 챕터 내용을 결합하여 인용구 요약을 시작합니다. 챕터 수: {}", chapterContents.size());
        List<String> chapterSummaries = summarizeChapters(chapterContents);

        // 모든 챕터 요약을 하나의 텍스트로 결합
        String combinedSummary = String.join("\n\n", chapterSummaries);

        // 결합된 텍스트를 인용구로 다시 요약
        return summarizeQuote(combinedSummary);
    }

    /**
     * 요약 요청 DTO
     */
    public record SummarizeRequest(String text) {
    }

    /**
     * 요약 응답 DTO
     */
    public record SummarizeResponse(String summary) {
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