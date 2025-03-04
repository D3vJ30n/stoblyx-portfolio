package com.j30n.stoblyx.adapter.out.persistence.ai;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FallbackMechanismTest {

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
        when(pexelsClient.searchImage(query)).thenThrow(new RuntimeException("타임아웃 발생"));
        
        // When
        String result = aiAdapter.searchImage(query);
        
        // Then
        assertThat(result).isEqualTo("static/images/fallback/book-cover.jpg");
        verify(pexelsClient).searchImage(query);
    }
    
    @Test
    @DisplayName("비동기 처리 중 일부 실패 시 부분 결과 반환")
    void asyncProcessing_PartialFailure_ReturnsPartialResults() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = "Test Book";
        String description = "Test Description";
        
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/audio.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("BGM 선택 실패"));
        
        // When
        CompletableFuture<BookMultimediaDTO> future = aiAdapter.generateBookMultimedia(title, description);
        BookMultimediaDTO result = future.get(5, TimeUnit.SECONDS);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("static/videos/fallback/book-animation.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/audio.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("static/bgm/neutral.mp3");
    }
} 