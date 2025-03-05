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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIIntegrationTest {

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
    @DisplayName("동기부여 책 멀티미디어 통합 생성 테스트")
    void generateMotivationBookMultimediaIntegrationTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/motivation-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/motivation-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/motivation-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/motivation-bgm.mp3");

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(MOTIVATION_TITLE, MOTIVATION_DESCRIPTION);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/motivation-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/motivation-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/motivation-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/motivation-bgm.mp3");
    }

    @Test
    @DisplayName("철학 책 멀티미디어 통합 생성 테스트")
    void generatePhilosophyBookMultimediaIntegrationTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/philosophy-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/philosophy-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/philosophy-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/philosophy-bgm.mp3");

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(PHILOSOPHY_TITLE, PHILOSOPHY_DESCRIPTION);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/philosophy-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/philosophy-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/philosophy-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/philosophy-bgm.mp3");
    }

    @Test
    @DisplayName("동기부여 책 비동기 멀티미디어 생성 성능 테스트")
    void motivationBookMultimediaPerformanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/motivation-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/motivation-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/motivation-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/motivation-bgm.mp3");

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(MOTIVATION_TITLE, MOTIVATION_DESCRIPTION);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // 종료 시간 기록 및 소요 시간 계산
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/motivation-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/motivation-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/motivation-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/motivation-bgm.mp3");

        // 비동기 처리로 인해 소요 시간이 짧아야 함
        assertThat(duration).isLessThan(1000); // 1초 미만이어야 함
    }

    @Test
    @DisplayName("철학 책 비동기 멀티미디어 생성 성능 테스트")
    void philosophyBookMultimediaPerformanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/philosophy-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/philosophy-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/philosophy-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/philosophy-bgm.mp3");

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(PHILOSOPHY_TITLE, PHILOSOPHY_DESCRIPTION);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // 종료 시간 기록 및 소요 시간 계산
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/philosophy-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/philosophy-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/philosophy-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/philosophy-bgm.mp3");

        // 비동기 처리로 인해 소요 시간이 짧아야 함
        assertThat(duration).isLessThan(1000); // 1초 미만이어야 함
    }

    @Test
    @DisplayName("한국어 콘텐츠 멀티미디어 생성 테스트")
    void koreanContentMultimediaTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String koreanTitle = "자기계발과 철학의 만남";
        String koreanDescription = "동기부여와 철학적 사고를 결합한 새로운 관점의 책입니다. 삶의 의미를 찾으며 성장하는 방법을 제시합니다.";

        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/korean-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/korean-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/korean-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/korean-bgm.mp3");

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(koreanTitle, koreanDescription);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/korean-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/korean-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/korean-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/korean-bgm.mp3");
    }

    @Test
    @DisplayName("멀티미디어 생성 중 일부 실패 시 폴백 리소스 반환 테스트")
    void generateMultimedia_PartialFailure_ReturnsFallbacks() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("BGM 선택 실패"));

        // When
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(MOTIVATION_TITLE, MOTIVATION_DESCRIPTION);

        // Then
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo(FALLBACK_VIDEO);
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo(FALLBACK_BGM);
    }
} 