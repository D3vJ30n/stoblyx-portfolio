package com.j30n.stoblyx.adapter.out.persistence.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class PexelsClientTest {

    @Mock
    private RestTemplate restTemplate;
    
    private PexelsClient pexelsClient;
    
    private static final String API_KEY = "563492ad6f91700001000001a9c7e2c0c3e04b3e9b5f6a8e7d4c3b2a1";
    
    // 테스트 상수
    private static final String MOTIVATION_TITLE = "성공하는 습관";
    private static final String MOTIVATION_DESCRIPTION = "매일 조금씩 성장하는 방법을 알려주는 동기부여 책입니다. 작은 습관이 큰 변화를 만듭니다.";
    
    private static final String PHILOSOPHY_TITLE = "존재의 의미";
    private static final String PHILOSOPHY_DESCRIPTION = "인간 존재의 본질과 삶의 의미에 대해 탐구하는 철학 서적입니다. 실존주의적 관점에서 삶을 바라봅니다.";
    
    @BeforeEach
    void setUp() {
        pexelsClient = new PexelsClient(API_KEY, restTemplate, new Random(42), new ObjectMapper());
    }
    
    @Test
    @DisplayName("동기부여 책 이미지 검색 테스트")
    void searchMotivationBookImage_Test() {
        // Given
        String query = MOTIVATION_TITLE + " " + MOTIVATION_DESCRIPTION;
        String mockResponse = "{"
                + "\"photos\": ["
                + "  {"
                + "    \"src\": {"
                + "      \"original\": \"https://example.com/motivation-image.jpg\""
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
        assertThat(result).isEqualTo("https://example.com/motivation-image.jpg");
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
                + "      \"original\": \"https://example.com/philosophy-image.jpg\""
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
        assertThat(result).isEqualTo("https://example.com/philosophy-image.jpg");
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
                + "        \"link\": \"https://example.com/motivation-video.mp4\""
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
        assertThat(result).isEqualTo("https://example.com/motivation-video.mp4");
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
                + "        \"link\": \"https://example.com/philosophy-video.mp4\""
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
        assertThat(result).isEqualTo("https://example.com/philosophy-video.mp4");
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
        )).thenThrow(new RuntimeException("API 호출 실패"));
        
        // When
        String result = pexelsClient.searchImage(query);
        
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://images.pexels.com/photos/3243/pen-calendar-to-do-checklist.jpg");
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
        )).thenThrow(new RuntimeException("API 호출 실패"));
        
        // When
        String result = pexelsClient.searchVideo(query);
        
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo("https://www.pexels.com/video/open-book-854381/");
    }
} 