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
class AIAdapterTest {

    @Mock
    private PexelsClient pexelsClient;
    
    @Mock
    private TTSClient ttsClient;
    
    @Mock
    private BGMClient bgmClient;
    
    @InjectMocks
    private AIAdapter aiAdapter;
    
    @Test
    @DisplayName("이미지 검색 성공 테스트")
    void searchImage_Success() {
        // Given
        String query = "book";
        String expectedUrl = "https://example.com/image.jpg";
        
        when(pexelsClient.searchImage(query)).thenReturn(expectedUrl);
        
        // When
        String result = aiAdapter.searchImage(query);
        
        // Then
        assertThat(result).isEqualTo(expectedUrl);
        verify(pexelsClient).searchImage(query);
    }
    
    @Test
    @DisplayName("이미지 검색 실패 시 폴백 이미지 반환")
    void searchImage_Failure_ReturnsFallback() {
        // Given
        String query = "book";
        
        when(pexelsClient.searchImage(query)).thenThrow(new RuntimeException("검색 실패"));
        
        // When
        String result = aiAdapter.searchImage(query);
        
        // Then
        assertThat(result).isEqualTo("static/images/fallback/book-cover.jpg");
        verify(pexelsClient).searchImage(query);
    }
    
    @Test
    @DisplayName("비디오 검색 성공 테스트")
    void searchVideo_Success() {
        // Given
        String query = "book";
        String expectedUrl = "https://example.com/video.mp4";
        
        when(pexelsClient.searchVideo(query)).thenReturn(expectedUrl);
        
        // When
        String result = aiAdapter.searchVideo(query);
        
        // Then
        assertThat(result).isEqualTo(expectedUrl);
        verify(pexelsClient).searchVideo(query);
    }
    
    @Test
    @DisplayName("음성 생성 성공 테스트")
    void generateSpeech_Success() {
        // Given
        String text = "This is a test";
        String expectedUrl = "https://example.com/speech.mp3";
        
        when(ttsClient.generateSpeech(text)).thenReturn(expectedUrl);
        
        // When
        String result = aiAdapter.generateSpeech(text);
        
        // Then
        assertThat(result).isEqualTo(expectedUrl);
        verify(ttsClient).generateSpeech(text);
    }
    
    @Test
    @DisplayName("BGM 선택 성공 테스트")
    void selectBGM_Success() {
        // Given
        String expectedUrl = "static/bgm/happy.mp3";
        
        when(bgmClient.selectBGM()).thenReturn(expectedUrl);
        
        // When
        String result = aiAdapter.selectBGM();
        
        // Then
        assertThat(result).isEqualTo(expectedUrl);
        verify(bgmClient).selectBGM();
    }
    
    @Test
    @DisplayName("멀티미디어 생성 통합 테스트")
    void generateBookMultimedia_Success() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = "Test Book";
        String description = "This is a test description";
        
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenReturn("https://example.com/video.mp4");
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenReturn("https://example.com/bgm.mp3");
        
        // When
        CompletableFuture<BookMultimediaDTO> futureResult = aiAdapter.generateBookMultimedia(title, description);
        BookMultimediaDTO result = futureResult.get(5, TimeUnit.SECONDS);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("https://example.com/video.mp4");
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("https://example.com/bgm.mp3");
    }
    
    @Test
    @DisplayName("멀티미디어 생성 중 일부 실패 시 부분 결과 반환")
    void generateBookMultimedia_PartialFailure() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String title = "Test Book";
        String description = "This is a test description";
        
        when(pexelsClient.searchImage(anyString())).thenReturn("https://example.com/image.jpg");
        when(pexelsClient.searchVideo(anyString())).thenThrow(new RuntimeException("비디오 검색 실패"));
        when(ttsClient.generateSpeech(anyString())).thenReturn("https://example.com/speech.mp3");
        when(bgmClient.selectBGMByText(anyString())).thenThrow(new RuntimeException("BGM 선택 실패"));
        
        // When
        CompletableFuture<BookMultimediaDTO> futureResult = aiAdapter.generateBookMultimedia(title, description);
        BookMultimediaDTO result = futureResult.get(5, TimeUnit.SECONDS);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getVideoUrl()).isEqualTo("static/videos/fallback/book-animation.mp4"); // 폴백 리소스
        assertThat(result.getAudioUrl()).isEqualTo("https://example.com/speech.mp3");
        assertThat(result.getBgmUrl()).isEqualTo("static/bgm/neutral.mp3");
    }
} 