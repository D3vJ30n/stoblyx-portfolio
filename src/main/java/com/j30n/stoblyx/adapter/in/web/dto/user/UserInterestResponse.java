package com.j30n.stoblyx.adapter.in.web.dto.user;

import com.j30n.stoblyx.domain.model.UserInterest;
import java.util.List;

public record UserInterestResponse(
    Long userId,
    List<String> genres,
    List<String> authors,
    List<String> keywords,
    String bio
) {
    public static UserInterestResponse from(UserInterest userInterest) {
        return new UserInterestResponse(
            userInterest.getUser().getId(),
            userInterest.getGenres(),
            userInterest.getAuthors(),
            userInterest.getKeywords(),
            null  // bio 필드가 없으므로 null로 설정하거나 다른 적절한 값 사용
        );
    }
    
    // 빌더 패턴 추가
    public static UserInterestResponseBuilder builder() {
        return new UserInterestResponseBuilder();
    }
    
    public static class UserInterestResponseBuilder {
        private Long userId;
        private List<String> genres;
        private List<String> authors;
        private List<String> keywords;
        private String bio;
        
        public UserInterestResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public UserInterestResponseBuilder genres(List<String> genres) { this.genres = genres; return this; }
        public UserInterestResponseBuilder authors(List<String> authors) { this.authors = authors; return this; }
        public UserInterestResponseBuilder keywords(List<String> keywords) { this.keywords = keywords; return this; }
        public UserInterestResponseBuilder bio(String bio) { this.bio = bio; return this; }
        
        public UserInterestResponse build() {
            return new UserInterestResponse(userId, genres, authors, keywords, bio);
        }
    }
}