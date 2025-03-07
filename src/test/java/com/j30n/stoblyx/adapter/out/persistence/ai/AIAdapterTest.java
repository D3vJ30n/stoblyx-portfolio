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
class AIAdapterTest {

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
    @DisplayName("동기부여 책 이미지 검색 테스트")
    void searchMotivationBookImage_Test() {
        // Given
        String query = MOTIVATION_TITLE + " " + MOTIVATION_DESCRIPTION;
        String expectedUrl = "https://example.com/motivation-image.jpg";

        when(pexelsClient.searchImage(query)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("철학 책 이미지 검색 테스트")
    void searchPhilosophyBookImage_Test() {
        // Given
        String query = PHILOSOPHY_TITLE + " " + PHILOSOPHY_DESCRIPTION;
        String expectedUrl = "https://example.com/philosophy-image.jpg";

        when(pexelsClient.searchImage(query)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("동기부여 책 비디오 검색 테스트")
    void searchMotivationBookVideo_Test() {
        // Given
        String query = MOTIVATION_TITLE;
        String expectedUrl = "https://example.com/motivation-video.mp4";

        when(pexelsClient.searchVideo(query)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("철학 책 비디오 검색 테스트")
    void searchPhilosophyBookVideo_Test() {
        // Given
        String query = PHILOSOPHY_TITLE;
        String expectedUrl = "https://example.com/philosophy-video.mp4";

        when(pexelsClient.searchVideo(query)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("동기부여 책 설명 음성 생성 테스트")
    void generateMotivationBookSpeech_Test() {
        // Given
        String text = MOTIVATION_DESCRIPTION;
        String expectedUrl = "https://example.com/motivation-speech.mp3";

        when(ttsClient.generateSpeech(text)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.generateSpeech(text);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("철학 책 설명 음성 생성 테스트")
    void generatePhilosophyBookSpeech_Test() {
        // Given
        String text = PHILOSOPHY_DESCRIPTION;
        String expectedUrl = "https://example.com/philosophy-speech.mp3";

        when(ttsClient.generateSpeech(text)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.generateSpeech(text);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("동기부여 책 텍스트 기반 BGM 선택 테스트")
    void selectMotivationBookBGM_Test() {
        // Given
        String text = MOTIVATION_DESCRIPTION;
        String expectedUrl = "https://example.com/motivation-bgm.mp3";

        when(bgmClient.selectBGMByText(text)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.selectBGMByText(text);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("철학 책 텍스트 기반 BGM 선택 테스트")
    void selectPhilosophyBookBGM_Test() {
        // Given
        String text = PHILOSOPHY_DESCRIPTION;
        String expectedUrl = "https://example.com/philosophy-bgm.mp3";

        when(bgmClient.selectBGMByText(text)).thenReturn(expectedUrl);

        // When
        String result = aiAdapter.selectBGMByText(text);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("동기부여 책 멀티미디어 생성 통합 테스트")
    void generateMotivationBookMultimedia_Test() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = MOTIVATION_TITLE;
        String description = MOTIVATION_DESCRIPTION;

        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/motivation-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/motivation-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/motivation-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/motivation-bgm.mp3");

        // When
        CompletableFuture<BookMediaResponse> futureResult = aiAdapter.generateBookMultimedia(title, description);
        BookMediaResponse result = futureResult.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/motivation-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/motivation-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/motivation-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/motivation-bgm.mp3");
    }

    @Test
    @DisplayName("철학 책 멀티미디어 생성 통합 테스트")
    void generatePhilosophyBookMultimedia_Test() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = PHILOSOPHY_TITLE;
        String description = PHILOSOPHY_DESCRIPTION;

        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/philosophy-image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/philosophy-video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/philosophy-speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/philosophy-bgm.mp3");

        // When
        CompletableFuture<BookMediaResponse> futureResult = aiAdapter.generateBookMultimedia(title, description);
        BookMediaResponse result = futureResult.get(5, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/philosophy-image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/philosophy-video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/philosophy-speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/philosophy-bgm.mp3");
    }

    @Test
    @DisplayName("이미지 검색 실패 시 폴백 이미지 반환 테스트")
    void searchImage_Failure_ReturnsFallback() {
        // Given
        String query = MOTIVATION_TITLE;

        when(pexelsClient.searchImage(query)).thenThrow(new RuntimeException("이미지 검색 실패"));

        // When
        String result = aiAdapter.searchImage(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(FALLBACK_IMAGE);
    }

    @Test
    @DisplayName("비디오 검색 실패 시 폴백 비디오 반환 테스트")
    void searchVideo_Failure_ReturnsFallback() {
        // Given
        String query = PHILOSOPHY_TITLE;

        when(pexelsClient.searchVideo(query)).thenThrow(new RuntimeException("비디오 검색 실패"));

        // When
        String result = aiAdapter.searchVideo(query);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(FALLBACK_VIDEO);
    }

    @Test
@DisplayName("오디오 생성 실패 시 폴백 오디오 반환 테스트")
void generateSpeech_Failure_ReturnsFallback() {
    // Given
    String text = MOTIVATION_DESCRIPTION;
    when(ttsClient.generateSpeech(text)).thenThrow(new RuntimeException("오디오 생성 실패"));
    
    // When
    String result = aiAdapter.generateSpeech(text);
    
    // Then
    assertNotNull(result);
    assertThat(result).isEqualTo(FALLBACK_AUDIO);
}

@Test
@DisplayName("BGM 선택 실패 시 폴백 BGM 반환 테스트")
void selectBGM_Failure_ReturnsFallback() {
    // Given
        String text = PHILOSOPHY_DESCRIPTION;
        when(bgmClient.selectBGMByText(text)).thenThrow(new RuntimeException("BGM 선택 실패"));

        // When
        String result = aiAdapter.selectBGMByText(text);

        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(FALLBACK_BGM);
    }
} 