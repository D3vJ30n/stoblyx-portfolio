package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse.BookInfo;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse.UserInfo;
import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 테스트 환경에서만 사용되는 컨트롤러
 * 인증이 필요한 엔드포인트를 인증 없이 호출할 수 있도록 합니다.
 * K6 테스트에 필요한 엔드포인트도 추가되었습니다.
 */
@RestController
@Profile("test")
@Slf4j
public class TestController {

    /**
     * 하드코딩된 응답을 반환하는 테스트용 엔드포인트
     */
    @GetMapping("/test/quotes")
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getQuotesForTest(
        @RequestParam(required = false) Long userId
    ) {
        log.info("테스트 엔드포인트 호출: /test/quotes, userId={}", userId);
        
        try {
            // 테스트 데이터 생성
            List<QuoteResponse> quotes = new ArrayList<>();
            
            // UserInfo 생성
            UserInfo userInfo = new UserInfo(
                1L, 
                "testuser",
                "테스트 사용자",
                "http://example.com/profile.jpg"
            );
            
            // BookInfo 생성
            BookInfo bookInfo = new BookInfo(
                1L,
                "테스트 책 제목",
                "테스트 저자",
                "http://example.com/book.jpg"
            );
            
            // 테스트용 응답 데이터 추가
            quotes.add(new QuoteResponse(
                1L, 
                "테스트 문구 내용입니다.", 
                "테스트 메모", 
                123, 
                5,  // likeCount
                2,  // saveCount
                false, // isLiked
                false, // isSaved
                userInfo,
                bookInfo,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));
            
            Page<QuoteResponse> page = new PageImpl<>(quotes);
            
            log.info("테스트 엔드포인트 응답: 성공, 데이터 크기={}", page.getSize());
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구 목록을 성공적으로 조회했습니다.", page));
        } catch (Exception e) {
            log.error("테스트 엔드포인트 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("ERROR", "문구 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * K6 테스트용 사용자 프로필 API
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserProfile(@PathVariable Long userId) {
        log.info("K6 테스트용 사용자 프로필 조회: userId={}", userId);
        
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("id", userId);
        userProfile.put("username", "testuser_" + userId);
        userProfile.put("nickname", "테스트 사용자 " + userId);
        userProfile.put("email", "user" + userId + "@example.com");
        userProfile.put("profileImage", "http://example.com/profile.jpg");
        userProfile.put("createdAt", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 프로필을 성공적으로 조회했습니다.", userProfile));
    }

    /**
     * K6 테스트용 콘텐츠 상호작용 API
     */
    @PostMapping({"/contents/interaction", "/interactions", "/user-interactions"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordContentInteraction(@RequestBody Map<String, Object> request) {
        log.info("K6 테스트용 콘텐츠 상호작용 기록: request={}", request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("interactionId", 1L);
        result.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "콘텐츠 상호작용이 성공적으로 기록되었습니다.", result));
    }

    /**
     * K6 테스트용 콘텐츠 좋아요 API
     */
    @PostMapping({"/contents/{contentId}/like", "/contents/{contentId}/likes", "/likes/content/{contentId}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> likeContent(@PathVariable Long contentId) {
        log.info("K6 테스트용 콘텐츠 좋아요: contentId={}", contentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("contentId", contentId);
        result.put("liked", true);
        
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "콘텐츠 좋아요가 성공적으로 처리되었습니다.", result));
    }

    /**
     * K6 테스트용 콘텐츠 북마크 API
     */
    @PostMapping({"/contents/{contentId}/bookmark", "/contents/{contentId}/bookmarks", "/bookmarks/content/{contentId}"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookmarkContent(@PathVariable Long contentId) {
        log.info("K6 테스트용 콘텐츠 북마크: contentId={}", contentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("contentId", contentId);
        result.put("bookmarked", true);
        
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "콘텐츠 북마크가 성공적으로 처리되었습니다.", result));
    }

    /**
     * K6 테스트용 사용자 점수 조회 API
     */
    @GetMapping("/ranking/user/{userId}/score")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserScore(@PathVariable Long userId) {
        log.info("K6 테스트용 사용자 점수 조회: userId={}", userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("totalScore", 1000);
        result.put("rank", 5);
        result.put("rankType", "GOLD");
        
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 점수를 성공적으로 조회했습니다.", result));
    }
} 