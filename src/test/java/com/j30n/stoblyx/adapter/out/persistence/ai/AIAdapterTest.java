package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookMediaResponse;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

/**
 * AIAdapter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AIAdapter 테스트")
class AIAdapterTest {

    @Mock
    private PexelsClient pexelsClient;

    @Mock
    private TTSClient ttsClient;

    @Mock
    private BGMClient bgmClient;

    @InjectMocks
    private AIAdapter aiAdapter;

    private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";
    private static final String TEST_VIDEO_URL = "https://example.com/video.mp4";
    private static final String TEST_AUDIO_URL = "https://example.com/audio.mp3";
    private static final String TEST_BGM_URL = "https://example.com/bgm.mp3";
    private static final String TEST_QUERY = "테스트 쿼리";
    private static final String TEST_TEXT = "테스트 텍스트";

    @BeforeEach
    void setUp() {
        // 테스트 설정
    }

    @Test
    @DisplayName("이미지 검색 성공 테스트")
    void searchImage_Success() {
        // given
        when(pexelsClient.searchImage(TEST_QUERY)).thenReturn(TEST_IMAGE_URL);

        // when
        String result = aiAdapter.searchImage(TEST_QUERY);

        // then
        assertThat(result).isEqualTo(TEST_IMAGE_URL);
        verify(pexelsClient, times(1)).searchImage(TEST_QUERY);
    }

    @Test
    @DisplayName("이미지 검색 실패 시 폴백 이미지 반환 테스트")
    void searchImage_WhenFailed_ShouldReturnFallbackImage() {
        // given
        when(pexelsClient.searchImage(TEST_QUERY)).thenThrow(new RuntimeException("[테스트용] API 오류"));

        // when
        String result = aiAdapter.searchImage(TEST_QUERY);

        // then
        assertThat(result).isEqualTo("static/images/fallback/book-cover.jpg");
        verify(pexelsClient, times(1)).searchImage(TEST_QUERY);
    }

    @Test
    @DisplayName("비디오 검색 성공 테스트")
    void searchVideo_Success() {
        // given
        when(pexelsClient.searchVideo(TEST_QUERY)).thenReturn(TEST_VIDEO_URL);

        // when
        String result = aiAdapter.searchVideo(TEST_QUERY);

        // then
        assertThat(result).isEqualTo(TEST_VIDEO_URL);
        verify(pexelsClient, times(1)).searchVideo(TEST_QUERY);
    }

    @Test
    @DisplayName("비디오 검색 실패 시 폴백 비디오 반환 테스트")
    void searchVideo_WhenFailed_ShouldReturnFallbackVideo() {
        // given
        when(pexelsClient.searchVideo(TEST_QUERY)).thenThrow(new RuntimeException("[테스트용] API 오류"));

        // when
        String result = aiAdapter.searchVideo(TEST_QUERY);

        // then
        assertThat(result).isEqualTo("static/videos/fallback/book-animation.mp4");
        verify(pexelsClient, times(1)).searchVideo(TEST_QUERY);
    }

    @Test
    @DisplayName("음성 생성 성공 테스트")
    void generateSpeech_Success() {
        // given
        when(ttsClient.generateSpeech(TEST_TEXT)).thenReturn(TEST_AUDIO_URL);

        // when
        String result = aiAdapter.generateSpeech(TEST_TEXT);

        // then
        assertThat(result).isEqualTo(TEST_AUDIO_URL);
        verify(ttsClient, times(1)).generateSpeech(TEST_TEXT);
    }

    @Test
    @DisplayName("음성 생성 실패 시 폴백 오디오 반환 테스트")
    void generateSpeech_WhenFailed_ShouldReturnFallbackAudio() {
        // given
        when(ttsClient.generateSpeech(TEST_TEXT)).thenThrow(new RuntimeException("[테스트용] TTS 오류"));

        // when
        String result = aiAdapter.generateSpeech(TEST_TEXT);

        // then
        assertThat(result).isEqualTo("static/audio/fallback/default-narration.mp3");
        verify(ttsClient, times(1)).generateSpeech(TEST_TEXT);
    }

    @Test
    @DisplayName("BGM 선택 성공 테스트")
    void selectBGM_Success() {
        // given
        when(bgmClient.selectBGM()).thenReturn(TEST_BGM_URL);

        // when
        String result = aiAdapter.selectBGM();

        // then
        assertThat(result).isEqualTo(TEST_BGM_URL);
        verify(bgmClient, times(1)).selectBGM();
    }

    @Test
    @DisplayName("BGM 선택 실패 시 폴백 BGM 반환 테스트")
    void selectBGM_WhenFailed_ShouldReturnFallbackBGM() {
        // given
        when(bgmClient.selectBGM()).thenThrow(new RuntimeException("[테스트용] BGM 오류"));

        // when
        String result = aiAdapter.selectBGM();

        // then
        assertThat(result).isEqualTo("static/bgm/neutral.mp3");
        verify(bgmClient, times(1)).selectBGM();
    }

    @Test
    @DisplayName("텍스트 기반 BGM 선택 성공 테스트")
    void selectBGMByText_Success() {
        // given
        when(bgmClient.selectBGMByText(TEST_TEXT)).thenReturn(TEST_BGM_URL);

        // when
        String result = aiAdapter.selectBGMByText(TEST_TEXT);

        // then
        assertThat(result).isEqualTo(TEST_BGM_URL);
        verify(bgmClient, times(1)).selectBGMByText(TEST_TEXT);
    }

    @Test
    @DisplayName("책 멀티미디어 생성 성공 테스트")
    void generateBookMultimedia_Success() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        String title = "테스트 책 제목";
        String description = "테스트 책 설명";
        
        when(pexelsClient.searchImage(contains(title))).thenReturn(TEST_IMAGE_URL);
        when(pexelsClient.searchVideo(title)).thenReturn(TEST_VIDEO_URL);
        when(ttsClient.generateSpeech(description)).thenReturn(TEST_AUDIO_URL);
        when(bgmClient.selectBGMByText(description)).thenReturn(TEST_BGM_URL);

        // when
        CompletableFuture<BookMediaResponse> future = aiAdapter.generateBookMultimedia(title, description);
        BookMediaResponse result = future.get(5, TimeUnit.SECONDS);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(TEST_IMAGE_URL);
        assertThat(result.getVideoUrl()).isEqualTo(TEST_VIDEO_URL);
        assertThat(result.getAudioUrl()).isEqualTo(TEST_AUDIO_URL);
        assertThat(result.getBgmUrl()).isEqualTo(TEST_BGM_URL);
    }
} 