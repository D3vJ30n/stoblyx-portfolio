package com.j30n.stoblyx.adapter.in.web.dto.user;

import com.j30n.stoblyx.domain.model.User;

public record UserProfileResponse(
    Long id,
    String username,
    String nickname,
    String email,
    String role,
    String profileImageUrl
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getRole().toString(),
            user.getProfileImageUrl()
        );
    }
    
    // 빌더 패턴 추가
    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }
    
    public static class UserProfileResponseBuilder {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String role;
        private String profileImageUrl;
        
        public UserProfileResponseBuilder id(Long id) { this.id = id; return this; }
        public UserProfileResponseBuilder username(String username) { this.username = username; return this; }
        public UserProfileResponseBuilder nickname(String nickname) { this.nickname = nickname; return this; }
        public UserProfileResponseBuilder email(String email) { this.email = email; return this; }
        public UserProfileResponseBuilder role(String role) { this.role = role; return this; }
        public UserProfileResponseBuilder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }
        
        public UserProfileResponse build() {
            return new UserProfileResponse(id, username, nickname, email, role, profileImageUrl);
        }
    }
} 