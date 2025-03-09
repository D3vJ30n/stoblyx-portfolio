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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 환경에서만 사용되는 컨트롤러
 * 인증이 필요한 엔드포인트를 인증 없이 호출할 수 있도록 합니다.
 */
@RestController
@RequestMapping("/test")
@Profile("test")
@Slf4j
public class TestController {

    /**
     * 하드코딩된 응답을 반환하는 테스트용 엔드포인트
     */
    @GetMapping("/quotes")
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
} 