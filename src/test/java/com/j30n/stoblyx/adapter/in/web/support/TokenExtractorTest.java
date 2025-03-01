package com.j30n.stoblyx.adapter.in.web.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TokenExtractorTest {

    @InjectMocks
    private TokenExtractor tokenExtractor;

    @Test
    @DisplayName("유효한 Bearer 토큰에서 토큰값 추출 성공")
    void extractToken_ValidBearerToken_ReturnsToken() {
        // Given
        String bearerToken = "Bearer token123";
        
        // When
        String token = tokenExtractor.extractToken(bearerToken);
        
        // Then
        assertThat(token).isEqualTo("token123");
    }

    @Test
    @DisplayName("토큰 형식이 유효하지 않은 경우 예외 발생")
    void extractToken_InvalidFormat_ThrowsException() {
        // Given
        String invalidToken = "Invalid token123";
        
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(invalidToken))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 토큰 형식");
    }

    @Test
    @DisplayName("빈 토큰 문자열에 대한 처리")
    void extractToken_EmptyToken_ThrowsException() {
        // Given
        String emptyToken = "Bearer ";
        
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(emptyToken))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("토큰 값이 비어있습니다");
    }
    
    @Test
    @DisplayName("null 토큰에 대한 처리")
    void extractToken_NullToken_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> tokenExtractor.extractToken(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("토큰이 null입니다");
    }
} 