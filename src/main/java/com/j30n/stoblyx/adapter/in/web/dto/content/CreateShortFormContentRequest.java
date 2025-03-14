package com.j30n.stoblyx.adapter.in.web.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 숏폼 콘텐츠 생성 요청 DTO
 */
public record CreateShortFormContentRequest(
    @NotNull(message = "책 ID는 필수입니다.")
    Long bookId,
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    String title,
    
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 1000자 이내로 입력해주세요.")
    String content,
    
    String emotionType,
    
    boolean autoEmotionAnalysis
) {
    /**
     * 감정 유형 상수
     */
    public static final class EmotionType {
        public static final String HAPPY = "HAPPY";
        public static final String SAD = "SAD";
        public static final String ANGRY = "ANGRY";
        public static final String FEAR = "FEAR";
        public static final String SURPRISE = "SURPRISE";
        public static final String DISGUST = "DISGUST";
        public static final String NEUTRAL = "NEUTRAL";
        
        private EmotionType() {
            // 인스턴스화 방지
        }
    }
} 