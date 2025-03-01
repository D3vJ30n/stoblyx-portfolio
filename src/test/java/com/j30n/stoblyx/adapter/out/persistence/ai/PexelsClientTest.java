package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PexelsClientTest {

    // 테스트용 상수 정의 (실제 PexelsClient 클래스의 상수와 동일한 값)
    private static final String FALLBACK_IMAGE = "https://images.pexels.com/photos/3243/pen-calendar-to-do-checklist.jpg";
    private static final String FALLBACK_VIDEO = "https://www.pexels.com/video/open-book-854381/";
    
    @Mock
    private RestTemplate restTemplate;
    
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @InjectMocks
    private PexelsClient pexelsClient;

    @BeforeEach
    void setUp() {
        // API 키만 설정
        ReflectionTestUtils.setField(pexelsClient, "apiKey", "test-api-key");
    }

    @Test
    @DisplayName("이미지 검색 성공 테스트")
    void searchImage_Success() throws Exception {
        // Given
        String query = "book";
        String mockResponseJson = "{\"photos\":[{\"src\":{\"original\":\"https://example.com/image.jpg\"}}]}";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);

        // RestTemplate 응답 설정
        when(restTemplate.exchange(
            contains("api.pexels.com/v1/search"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class))
        ).thenReturn(mockResponse);

        // When
        String result = pexelsClient.searchImage(query);

        // Then
        assertThat(result).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("이미지 검색 실패 시 폴백 이미지 반환")
    void searchImage_Failure_ReturnsEmpty() {
        // Given
        String query = "book";

        // RestTemplate이 예외를 던지도록 설정
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class))
        ).thenThrow(new RestClientException("API 호출 실패"));

        // When
        String result = pexelsClient.searchImage(query);

        // Then
        assertThat(result).isEqualTo(FALLBACK_IMAGE);
    }

    @Test
    @DisplayName("비디오 검색 성공 테스트")
    void searchVideo_Success() throws Exception {
        // Given
        String query = "book";
        String mockResponseJson = "{\"videos\":[{\"video_files\":[{\"link\":\"https://example.com/video.mp4\"}]}]}";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);

        // RestTemplate 응답 설정
        lenient().when(restTemplate.exchange(
            contains("api.pexels.com/videos/search"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class))
        ).thenReturn(mockResponse);

        // When
        String result = pexelsClient.searchVideo(query);

        // Then
        assertThat(result).isEqualTo("https://example.com/video.mp4");
    }
} 