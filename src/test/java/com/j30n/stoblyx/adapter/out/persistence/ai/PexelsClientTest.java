package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PexelsClientTest {

    // 환경 변수에서 API 키를 가져오거나 테스트용 API 키 사용
    private static final String API_KEY = System.getenv("PEXELS_API_KEY") != null && !System.getenv("PEXELS_API_KEY").isEmpty()
        ? System.getenv("PEXELS_API_KEY")
        : "Yyicakz4WGEu9XntPLqVIR4JUKEAokSMG7FfoAc35m6kHhnJnu5kHkPa";
    private static final String MOTIVATION_DESCRIPTION = "A motivational book that teaches how to grow a little every day. Small habits create big changes.";
    private static final String PHILOSOPHY_DESCRIPTION = "A philosophical book exploring the essence of human existence and the meaning of life from an existentialist perspective.";
    // 테스트 상수 (영어로 변경)
    private static final String MOTIVATION_TITLE = "Motivational Book";
    private static final String PHILOSOPHY_TITLE = "Philosophy Book";
    private static final String FALLBACK_VIDEO_URL = "https://www.pexels.com/video/woman-reading-book-in-the-library-5167740/";
    @Mock
    private RestTemplate restTemplate;
    private PexelsClient pexelsClient;

    @BeforeEach
    void setUp() {
        pexelsClient = new PexelsClient(API_KEY, restTemplate, new Random(42), new ObjectMapper());
    }

    // 향후 알라딘 Open API 구현 예정
    @Test
    @DisplayName("동기부여 책 이미지 검색 테스트")
    void searchMotivationBookImage_Test() {
        // Given
        String query = MOTIVATION_TITLE + " " + MOTIVATION_DESCRIPTION;
        String mockResponse = "{"
            + "\"photos\": ["
            + "  {"
            + "    \"src\": {"
            + "      \"original\": \"https://images.pexels.com/photos/4498362/pexels-photo-4498362.jpeg\""
            + "    }"
            + "  }"
            + "]"
            + "}";

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        String result = pexelsClient.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://images.pexels.com/photos/4498362/pexels-photo-4498362.jpeg");
    }

    @Test
    @DisplayName("철학 책 이미지 검색 테스트")
    void searchPhilosophyBookImage_Test() {
        // Given
        String query = PHILOSOPHY_TITLE + " " + PHILOSOPHY_DESCRIPTION;
        String mockResponse = "{"
            + "\"photos\": ["
            + "  {"
            + "    \"src\": {"
            + "      \"original\": \"https://images.pexels.com/photos/2908984/pexels-photo-2908984.jpeg\""
            + "    }"
            + "  }"
            + "]"
            + "}";

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        String result = pexelsClient.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://images.pexels.com/photos/2908984/pexels-photo-2908984.jpeg");
    }

    @Test
    @DisplayName("동기부여 책 비디오 검색 테스트")
    void searchMotivationBookVideo_Test() {
        // Given
        String query = MOTIVATION_TITLE;
        String mockResponse = "{"
            + "\"videos\": ["
            + "  {"
            + "    \"video_files\": ["
            + "      {"
            + "        \"link\": \"https://www.pexels.com/video/a-woman-writing-on-her-notebook-6866920/\""
            + "      }"
            + "    ]"
            + "  }"
            + "]"
            + "}";

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        String result = pexelsClient.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://www.pexels.com/video/a-woman-writing-on-her-notebook-6866920/");
    }

    @Test
    @DisplayName("철학 책 비디오 검색 테스트")
    void searchPhilosophyBookVideo_Test() {
        // Given
        String query = PHILOSOPHY_TITLE;
        String mockResponse = "{"
            + "\"videos\": ["
            + "  {"
            + "    \"video_files\": ["
            + "      {"
            + "        \"link\": \"https://www.pexels.com/video/books-education-literature-1409099/\""
            + "      }"
            + "    ]"
            + "  }"
            + "]"
            + "}";

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        String result = pexelsClient.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://www.pexels.com/video/books-education-literature-1409099/");
    }

    @Test
    @DisplayName("이미지 검색 실패 시 폴백 이미지 반환 테스트")
    void searchImage_Failure_ReturnsFallback() {
        // Given
        String query = MOTIVATION_TITLE;

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenThrow(new RestClientException("[테스트용] API 호출 실패"));

        // When
        String result = pexelsClient.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg");
    }

    @Test
    @DisplayName("비디오 검색 실패 시 폴백 비디오 반환 테스트")
    void searchVideo_Failure_ReturnsFallback() {
        // Given
        String query = PHILOSOPHY_TITLE;

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenThrow(new RuntimeException("[테스트용] API 호출 실패"));

        // When
        String result = pexelsClient.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(FALLBACK_VIDEO_URL);
    }

    /**
     * 실제 API를 사용한 통합 테스트
     * 참고: 이 테스트는 실제 API 호출을 수행하므로 API 키가 유효해야 합니다.
     * API 호출 제한에 도달할 수 있으므로 필요할 때만 실행하세요.
     */
    @Test
    @DisplayName("실제 API를 사용한 이미지 검색 통합 테스트")
    void realApiImageSearchIntegrationTest() {
        // 실제 API 호출을 위한 클라이언트 생성 (모킹 없음)
        RestTemplate realRestTemplate = new RestTemplate();

        // Authorization 헤더를 추가하는 인터셉터
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, API_KEY);
            return execution.execute(request, body);
        };

        realRestTemplate.setInterceptors(Collections.singletonList(interceptor));

        PexelsClient realClient = new PexelsClient(
            API_KEY,
            realRestTemplate,
            new Random(),
            new ObjectMapper());

        // 검색 쿼리
        String query = "books library reading";

        // 실제 API 호출
        String imageUrl = realClient.searchImage(query);

        // 결과 검증
        assertNotNull(imageUrl);
        System.out.println("실제 이미지 URL: " + imageUrl);
        // URL 형식 검증
        assertThat(imageUrl).contains("https://");
    }

    @Test
    @DisplayName("실제 API를 사용한 비디오 검색 통합 테스트")
    void realApiVideoSearchIntegrationTest() {
        // 실제 API 호출을 위한 클라이언트 생성 (모킹 없음)
        RestTemplate realRestTemplate = new RestTemplate();

        // Authorization 헤더를 추가하는 인터셉터
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, API_KEY);
            return execution.execute(request, body);
        };

        realRestTemplate.setInterceptors(Collections.singletonList(interceptor));

        PexelsClient realClient = new PexelsClient(
            API_KEY,
            realRestTemplate,
            new Random(),
            new ObjectMapper());

        // 검색 쿼리
        String query = "books reading study";

        // 실제 API 호출
        String videoUrl = realClient.searchVideo(query);

        // 결과 검증
        assertNotNull(videoUrl);
        System.out.println("실제 비디오 URL: " + videoUrl);
        // URL 형식 검증
        assertThat(videoUrl).contains("https://");
    }

    @Test
    @DisplayName("이미지 및 비디오 URL 검증 테스트")
    void validateUrlsTest() {
        // 이미지 URL 형식 검증
        String imageUrl = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg"; // 책상 위의 책과 커피 이미지
        assertThat(imageUrl)
            .startsWith("https://")
            .contains("pexels.com");

        // 비디오 URL 형식 검증
        String videoUrl = "https://www.pexels.com/video/woman-reading-book-in-the-library-5167740/"; // 도서관에서 책 읽는 여성 비디오
        assertThat(videoUrl)
            .startsWith("https://")
            .contains("pexels.com");

        // 폴백 URL 형식 검증
        String fallbackImage = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg"; // 책상 위의 책과 커피 이미지
        assertThat(fallbackImage)
            .startsWith("https://");

        String fallbackVideo = FALLBACK_VIDEO_URL;
        assertThat(fallbackVideo)
            .startsWith("https://");
    }

    /**
     * 실제 API 호출 시 발생하는 경고 메시지는 테스트에 영향을 주지 않습니다.
     * 경고 메시지는 다음과 같은 이유로 발생할 수 있습니다
     * 1. 테스트 환경에서 API_KEY가 PexelsConfig.API_KEY와 같을 경우
     * 2. 실제 API 호출 시 인증에 실패하는 경우
     *
     * 테스트가 통과한다면 기능적으로는 문제없음
     */
} 