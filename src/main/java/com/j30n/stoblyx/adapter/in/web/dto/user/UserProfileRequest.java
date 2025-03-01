package com.j30n.stoblyx.adapter.in.web.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    
    @NotBlank(message = "사용자 이름을 입력해주세요")
    @Size(min = 2, max = 50, message = "사용자 이름은 2-50자 사이여야 합니다")
    private String username;
    
    private String profileImage;
    
    @Size(max = 500, message = "자기소개는 최대 500자까지 입력 가능합니다")
    private String bio;
} 