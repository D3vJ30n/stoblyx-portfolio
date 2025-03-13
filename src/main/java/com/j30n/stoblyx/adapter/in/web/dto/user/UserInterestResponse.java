package com.j30n.stoblyx.adapter.in.web.dto.user;

import com.j30n.stoblyx.domain.model.UserInterest;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public record UserInterestResponse(
    Long userId,
    List<String> genres,
    List<String> authors,
    List<String> keywords,
    String bio
) {
    public static UserInterestResponse from(UserInterest userInterest) {
        List<String> genres = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        
        parseInterestsJson(userInterest.getInterests(), genres, authors, keywords);
        
        return new UserInterestResponse(
            userInterest.getUser().getId(),
            genres,
            authors,
            keywords,
            null  // bio 필드가 없으므로 null로 설정
        );
    }
    
    private static void parseInterestsJson(String interestsJson, List<String> genres, List<String> authors, List<String> keywords) {
        if (interestsJson == null || interestsJson.isEmpty()) {
            return;
        }
        
        try {
            JSONObject json = new JSONObject(interestsJson);
            extractJsonArray(json, "genres", genres);
            extractJsonArray(json, "authors", authors);
            extractJsonArray(json, "keywords", keywords);
        } catch (JSONException e) {
            // JSON 파싱 오류 시 무시
        }
    }
    
    private static void extractJsonArray(JSONObject json, String key, List<String> target) {
        if (!json.has(key)) {
            return;
        }
        
        try {
            JSONArray array = json.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                target.add(array.getString(i));
            }
        } catch (JSONException e) {
            // 특정 배열 파싱 오류 시 무시
        }
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