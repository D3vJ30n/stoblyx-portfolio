package com.j30n.stoblyx.adapter.out.persistence.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("KoBartClient 테스트")
class KoBartClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KoBartClient koBartClient;

    @Test
    @DisplayName("KoBART API가 활성화되어 있을 때 요약이 성공적으로 수행되어야 한다")
    void summarize_WhenEnabled_ShouldSummarizeText() {
        // given
        String originalText = "이것은 요약할 긴 텍스트입니다. KoBART 모델을 통해 요약이 잘 되는지 테스트합니다.";
        String expectedSummary = "요약된 텍스트입니다.";

        // KoBART 활성화 및 API URL 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", true);
        ReflectionTestUtils.setField(koBartClient, "apiUrl", "http://localhost:8000");

        // RestTemplate mock 설정
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(expectedSummary));
        
        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(KoBartClient.SummarizeResponse.class)
        )).thenReturn(responseEntity);

        // when
        String result = koBartClient.summarize(originalText);

        // then
        assertThat(result).isEqualTo(expectedSummary);
    }

    @Test
    @DisplayName("KoBART API가 비활성화되어 있을 때 원본 텍스트가 반환되어야 한다")
    void summarize_WhenDisabled_ShouldReturnOriginalText() {
        // given
        String originalText = "이것은 요약할 텍스트입니다.";

        // KoBART 비활성화 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", false);

        // when
        String result = koBartClient.summarize(originalText);

        // then
        assertThat(result).isEqualTo(originalText);
    }

    @Test
    @DisplayName("챕터 내용 요약이 성공적으로 수행되어야 한다")
    void summarizeChapter_ShouldSummarizeChapterContent() {
        // given
        String chapterContent = "이것은 챕터 내용입니다. 요약이 필요한 긴 텍스트입니다.";
        String expectedSummary = "챕터 내용 요약입니다.";

        // KoBART 활성화 및 API URL 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", true);
        ReflectionTestUtils.setField(koBartClient, "apiUrl", "http://localhost:8000");

        // RestTemplate mock 설정
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(expectedSummary));
        
        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(KoBartClient.SummarizeResponse.class)
        )).thenReturn(responseEntity);

        // when
        String result = koBartClient.summarizeChapter(chapterContent);

        // then
        assertThat(result).isEqualTo(expectedSummary);
    }

    @Test
    @DisplayName("여러 챕터 내용 요약이 성공적으로 수행되어야 한다")
    void summarizeChapters_ShouldSummarizeMultipleChapters() {
        // given
        List<String> chapterContents = Arrays.asList(
            "첫 번째 챕터 내용입니다.",
            "두 번째 챕터 내용입니다."
        );

        // KoBART 활성화 및 API URL 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", true);
        ReflectionTestUtils.setField(koBartClient, "apiUrl", "http://localhost:8000");

        // RestTemplate mock 설정
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity1 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse("첫 챕터 요약"));
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity2 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse("둘째 챕터 요약"));

        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(KoBartClient.SummarizeResponse.class)
        )).thenReturn(responseEntity1, responseEntity2);

        // when
        List<String> results = koBartClient.summarizeChapters(chapterContents);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0)).isEqualTo("첫 챕터 요약");
        assertThat(results.get(1)).isEqualTo("둘째 챕터 요약");
    }

    @Test
    @DisplayName("챕터 내용과 인용구 요약이 연결되어 수행되어야 한다")
    void summarizeChapterAndQuote_ShouldPerformTwoStepSummarization() {
        // given
        String chapterContent = "이것은 챕터 내용입니다. 요약이 필요한 긴 텍스트입니다.";
        String chapterSummary = "챕터 요약 텍스트입니다.";
        String quoteSummary = "인용구 요약입니다.";

        // KoBART 활성화 및 API URL 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", true);
        ReflectionTestUtils.setField(koBartClient, "apiUrl", "http://localhost:8000");

        // RestTemplate mock 설정 - 첫 번째 요청에서는 챕터 요약을, 두 번째 요청에서는 인용구 요약을 반환
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity1 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(chapterSummary));
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity2 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(quoteSummary));

        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(KoBartClient.SummarizeResponse.class)
        )).thenReturn(responseEntity1, responseEntity2);

        // when
        String result = koBartClient.summarizeChapterAndQuote(chapterContent);

        // then
        assertThat(result).isEqualTo(quoteSummary);
    }

    @Test
    @DisplayName("여러 챕터 내용과 인용구 요약이 연결되어 수행되어야 한다")
    void summarizeChaptersAndQuote_ShouldSummarizeMultipleChaptersAndThenQuote() {
        // given
        List<String> chapterContents = Arrays.asList(
            "첫 번째 챕터 내용입니다.",
            "두 번째 챕터 내용입니다."
        );
        String firstChapterSummary = "첫 챕터 요약";
        String secondChapterSummary = "둘째 챕터 요약";
        String combinedQuoteSummary = "통합 인용구 요약";

        // KoBART 활성화 및 API URL 설정
        ReflectionTestUtils.setField(koBartClient, "enabled", true);
        ReflectionTestUtils.setField(koBartClient, "apiUrl", "http://localhost:8000");

        // RestTemplate mock 설정
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity1 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(firstChapterSummary));
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity2 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(secondChapterSummary));
        ResponseEntity<KoBartClient.SummarizeResponse> responseEntity3 = 
            ResponseEntity.ok(new KoBartClient.SummarizeResponse(combinedQuoteSummary));

        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(KoBartClient.SummarizeResponse.class)
        )).thenReturn(responseEntity1, responseEntity2, responseEntity3);

        // when
        String result = koBartClient.summarizeChaptersAndQuote(chapterContents);

        // then
        assertThat(result).isEqualTo(combinedQuoteSummary);
    }
} 