package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse.BookInfo;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse.UserInfo;
import com.j30n.stoblyx.application.service.book.BookService;
import com.j30n.stoblyx.application.service.quote.QuoteService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트에서 사용할 모킹된 빈들을 제공하는 설정 클래스
 */
@TestConfiguration
@Profile("test")
public class MockTestConfig {

    /**
     * 테스트용으로 모킹된 QuoteService 빈
     */
    @Bean
    @Primary
    public QuoteService mockQuoteService() {
        QuoteService mockService = Mockito.mock(QuoteService.class);
        
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
        
        // getQuotes 메소드 모킹
        Mockito.when(mockService.getQuotes(Mockito.anyLong(), Mockito.any(Pageable.class)))
               .thenReturn(new PageImpl<>(quotes));
        
        // getQuote 메소드 모킹
        Mockito.when(mockService.getQuote(Mockito.anyLong(), Mockito.anyLong()))
               .thenReturn(quotes.get(0));
               
        return mockService;
    }
    
    /**
     * 테스트용으로 모킹된 BookService 빈
     */
    @Bean
    @Primary
    public BookService mockBookService() {
        return Mockito.mock(BookService.class);
    }
} 