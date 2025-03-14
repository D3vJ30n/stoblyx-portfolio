package com.j30n.stoblyx.adapter.in.web.dto.content;

import jakarta.validation.constraints.NotNull;

/**
 * 콘텐츠 상호작용 요청 DTO
 */
public record ContentInteractionRequest(
    @NotNull(message = "콘텐츠 ID는 필수입니다.")
    Long contentId,
    
    @NotNull(message = "상호작용 유형은 필수입니다.")
    String interactionType
) {
    /**
     * 상호작용 유형 상수
     */
    public static final class InteractionType {
        public static final String VIEW = "VIEW";
        public static final String LIKE = "LIKE";
        public static final String SHARE = "SHARE";
        public static final String COMMENT = "COMMENT";
        public static final String BOOKMARK = "BOOKMARK";
        
        /**
         * 유효한 상호작용 타입인지 확인합니다.
         * 
         * @param type 확인할 상호작용 타입
         * @return 유효한 상호작용 타입이면 true, 아니면 false
         */
        public static boolean isValid(String type) {
            return VIEW.equals(type) || 
                   LIKE.equals(type) || 
                   SHARE.equals(type) || 
                   COMMENT.equals(type) || 
                   BOOKMARK.equals(type);
        }
        
        private InteractionType() {
            // 인스턴스화 방지
        }
    }
} 