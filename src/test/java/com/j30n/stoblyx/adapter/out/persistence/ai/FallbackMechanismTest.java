package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookMediaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FallbackMechanismTest {

    // 테스트 상수
    private static final String MOTIVATION_TITLE = "성공하는 습관";
    private static final String MOTIVATION_DESCRIPTION = "매일 조금씩 성장하는 방법을 알려주는 동기부여 책입니다. 작은 습관이 큰 변화를 만듭니다.";
    private static final String PHILOSOPHY_TITLE = "존재의 의미";
    private static final String PHILOSOPHY_DESCRIPTION = "인간 존재의 본질과 삶의 의미에 대해 탐구하는 철학 서적입니다. 실존주의적 관점에서 삶을 바라봅니다.";
    // 폴백 리소스 상수
    private static final String FALLBACK_IMAGE = "static/images/fallback/book-cover.jpg";
    private static final String FALLBACK_VIDEO = "static/videos/fallback/book-animation.mp4";
    private static final String FALLBACK_AUDIO = "static/audio/fallback/default-narration.mp3";
    private static final String FALLBACK_BGM = "static/bgm/neutral.mp3";
    @Mock
    private PexelsClient pexelsClient;
    @Mock
    private TTSClient ttsClient;
    @Mock
    private BGMClient bgmClient;
    @InjectMocks
    private AIAdapter aiAdapter;

    @Test
    @DisplayName("타임아웃 발생 시 폴백 메커니즘 작동 확인")
    void timeout_TriggersRallback() {
        // Given
        String query = "test book";

        // pexelsClient를 이용한 searchImage 호출 시 RuntimeException을 던지도록 설정
        when(pexelsClient.searchImage(query)).thenThrow(new RuntimeException("[테스트용] 타임아웃 발생"));

        // When
        String result = aiAdapter.searchImage(query);

        // Then
        assertThat(result).isEqualTo(FALLBACK_IMAGE);
        verify(pexelsClient).searchImage(query);
    }

    @Test
    @DisplayName("비동기 처리 중 일부 실패 시 부분 결과 반환")
    void asyncProcessing_PartialFailure_ReturnsPartialResults() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = "Test Book";
        String description = "Test Description";

        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("[테스트용] 비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/audio.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("[테스트용] BGM 선택 실패"));

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(title, description);
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo(FALLBACK_VIDEO);
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/audio.mp3");
        assertThat(result.getBgmUrl()).isEqualTo(FALLBACK_BGM);
    }

    @Test
    @DisplayName("동기부여 책 이미지 검색 실패 시 폴백 이미지 반환")
    void motivationBookImage_Failure_ReturnsFallback() {
        // Given
        String query = MOTIVATION_TITLE + " " + MOTIVATION_DESCRIPTION;

        when(pexelsClient.searchImage(query)).thenThrow(new RuntimeException("[테스트용] 이미지 검색 실패"));

        // When
        String result = aiAdapter.searchImage(query);

        // Then
        assertThat(result).isEqualTo(FALLBACK_IMAGE);
        verify(pexelsClient).searchImage(query);
    }

    @Test
    @DisplayName("철학 책 비디오 검색 실패 시 폴백 비디오 반환")
    void philosophyBookVideo_Failure_ReturnsFallback() {
        // Given
        String query = PHILOSOPHY_TITLE;

        when(pexelsClient.searchVideo(query)).thenThrow(new RuntimeException("[테스트용] 비디오 검색 실패"));

        // When
        String result = aiAdapter.searchVideo(query);

        // Then
        assertThat(result).isEqualTo(FALLBACK_VIDEO);
        verify(pexelsClient).searchVideo(query);
    }

    @Test
    @DisplayName("동기부여 책 멀티미디어 생성 중 모든 API 실패 시 폴백 리소스 반환")
    void motivationBookMultimedia_AllFailures_ReturnsFallbacks() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenThrow(new RuntimeException("[테스트용] 이미지 검색 실패"));
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("[테스트용] 비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenThrow(new RuntimeException("[테스트용] 음성 생성 실패"));
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("[테스트용] BGM 선택 실패"));

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(MOTIVATION_TITLE, MOTIVATION_DESCRIPTION);
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(FALLBACK_IMAGE);
        assertThat(result.getVideoUrl()).isEqualTo(FALLBACK_VIDEO);
        assertThat(result.getAudioUrl()).isEqualTo(FALLBACK_AUDIO);
        assertThat(result.getBgmUrl()).isEqualTo(FALLBACK_BGM);
    }

    @Test
    @DisplayName("철학 책 멀티미디어 생성 중 일부 API 실패 시 부분 폴백 리소스 반환")
    void philosophyBookMultimedia_PartialFailures_ReturnsPartialFallbacks() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/philosophy-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("[테스트용] 비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/philosophy-audio.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("[테스트용] BGM 선택 실패"));

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(PHILOSOPHY_TITLE, PHILOSOPHY_DESCRIPTION);
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/philosophy-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo(FALLBACK_VIDEO);
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/philosophy-audio.mp3");
        assertThat(result.getBgmUrl()).isEqualTo(FALLBACK_BGM);
    }
} 