package com.j30n.stoblyx.adapter.in.web.dto.reward;

/**
 * 보상 수령 요청을 위한 DTO
 */
public record GamificationRewardClaimRequest(
    String redeemCode,
    String shippingAddress,
    String contactPhone
) {
    /**
     * 보상 수령 요청 DTO의 컴팩트 생성자
     */
    public GamificationRewardClaimRequest {
        // 필드 검증은 필요에 따라 추가
    }
} 