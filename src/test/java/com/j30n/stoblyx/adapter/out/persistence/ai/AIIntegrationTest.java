package com.j30n.stoblyx.adapter.out.persistence.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AIIntegrationTest {

    @Mock
    private PexelsClient pexelsClient;
    
    @Mock
    private TTSClient ttsClient;
    
    @Mock
    private BGMClient bgmClient;
    
    @InjectMocks
    private AIAdapter aiAdapter;
    
    @Test
    @DisplayName("비동기 멀티미디어 생성 통합 테스트")
    void generateBookMultimediaAsyncIntegrationTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = "스프링 부트 완벽 가이드";
        String description = "스프링 부트의 모든 기능을 상세히 설명하는 책입니다.";
        
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/bgm.mp3");
        
        // When
        CompletableFuture<BookMultimediaDTO> future = aiAdapter.generateBookMultimedia(title, description);
        
        // Then
        BookMultimediaDTO result = future.get(10, TimeUnit.SECONDS);
        
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/bgm.mp3");
    }
} 