package com.j30n.stoblyx.application.service.ai;

import com.j30n.stoblyx.application.port.out.ai.AIPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * AIService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AIService 테스트")
class AIServiceTest {

    @Mock
    private AIPort aiPort;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    void setUp() {
        // 테스트 설정
    }

    @Test
    @DisplayName("이미지 검색 테스트")
    void searchImage_ShouldReturnImageUrl() {
        // given
        String query = "책 표지";
        String expectedUrl = "https://example.com/image.jpg";
        when(aiPort.searchImage(query)).thenReturn(expectedUrl);

        // when
        String result = aiService.searchImage(query);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(aiPort, times(1)).searchImage(query);
    }

    @Test
    @DisplayName("비디오 검색 테스트")
    void searchVideo_ShouldReturnVideoUrl() {
        // given
        String query = "책 애니메이션";
        String expectedUrl = "https://example.com/video.mp4";
        when(aiPort.searchVideo(query)).thenReturn(expectedUrl);

        // when
        String result = aiService.searchVideo(query);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(aiPort, times(1)).searchVideo(query);
    }

    @Test
    @DisplayName("음성 생성 테스트")
    void generateSpeech_ShouldReturnAudioUrl() {
        // given
        String text = "이것은 테스트 텍스트입니다.";
        String expectedUrl = "https://example.com/audio.mp3";
        when(aiPort.generateSpeech(text)).thenReturn(expectedUrl);

        // when
        String result = aiService.generateSpeech(text);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(aiPort, times(1)).generateSpeech(text);
    }

    @Test
    @DisplayName("BGM 선택 테스트")
    void selectBGM_ShouldReturnBgmUrl() {
        // given
        String expectedUrl = "https://example.com/bgm.mp3";
        when(aiPort.selectBGM()).thenReturn(expectedUrl);

        // when
        String result = aiService.selectBGM();

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(aiPort, times(1)).selectBGM();
    }
} 