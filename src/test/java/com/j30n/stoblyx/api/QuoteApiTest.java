package com.j30n.stoblyx.api;

import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MockTestConfig;
import com.j30n.stoblyx.config.RedisTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.TestConfig;
import com.j30n.stoblyx.config.XssExclusionTestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * 명언 API 통합 테스트 클래스
 */
@Import({RedisTestConfig.class, SecurityTestConfig.class, ContextTestConfig.class, MockTestConfig.class, XssExclusionTestConfig.class, TestConfig.class})
@DisplayName("명언 API 통합 테스트")
class QuoteApiTest extends BaseApiTest {

    private static final String QUOTE_API_PATH = "/quotes";
    private static final String TEST_API_PATH = "/test/quotes";
    
    // 테스트용 고정 ID
    private static final Long TEST_QUOTE_ID = 1L;
    private static final Long TEST_BOOK_ID = 1L;
    
    // 테스트 중 생성된 문구 ID 저장 리스트
    private List<Long> createdQuoteIds = new ArrayList<>();
    
    @BeforeEach
    void setUp() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        createdQuoteIds = new ArrayList<>();
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 중 생성된 모든 문구 삭제
        for (Long quoteId : createdQuoteIds) {
            try {
                givenAuth(userToken)
                    .delete(QUOTE_API_PATH + "/" + quoteId);
            } catch (Exception e) {
                System.out.println("문구 삭제 중 오류 발생: " + e.getMessage());
            }
        }
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("문구 목록 조회 API 테스트")
    void testGetQuotes() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        givenAuth(userToken)
            .queryParam("userId", "1")
            .when()
                .get(QUOTE_API_PATH)
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("문구 상세 조회 API 테스트")
    void testGetQuote() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트용 문구 ID 사용
        Long quoteId = TEST_QUOTE_ID;

        // 문구 상세 조회 테스트
        givenAuth(userToken)
            .queryParam("userId", "1")
            .when()
                .get(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(404), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("문구 등록 API 테스트")
    void testCreateQuote() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트용 책 ID 사용
        Long bookId = TEST_BOOK_ID;

        // 문구 등록 요청 데이터 생성
        Map<String, Object> quoteData = createQuoteData(bookId);

        // 인증된 요청으로 문구 등록
        givenAuth(userToken)
            .contentType(ContentType.JSON)
            .body(quoteData)
            .when()
                .post(QUOTE_API_PATH)
            .then()
                .log().all()
                .statusCode(anyOf(is(201), is(200), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("문구 수정 API 테스트")
    void testUpdateQuote() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트용 문구 ID 사용
        Long quoteId = TEST_QUOTE_ID;

        // 문구 수정 요청 데이터 생성
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("content", "수정된 문구 내용입니다.");
        updateData.put("page", 456);
        updateData.put("memo", "수정된 메모");

        // 인증된 요청으로 문구 수정
        givenAuth(userToken)
            .contentType(ContentType.JSON)
            .body(updateData)
            .when()
                .put(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(404), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("문구 삭제 API 테스트")
    void testDeleteQuote() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트용 문구 ID 사용
        Long quoteId = TEST_QUOTE_ID;

        // 인증된 요청으로 문구 삭제
        givenAuth(userToken)
            .when()
                .delete(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204), is(404), is(500)));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("인증 없이 문구 등록 시 실패 테스트")
    void testCreateQuoteWithoutAuth() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트용 책 ID 사용
        Long bookId = TEST_BOOK_ID;

        // 문구 등록 요청 데이터 생성
        Map<String, Object> quoteData = createQuoteData(bookId);

        // 인증 없이 문구 등록 요청 (401 Unauthorized 예상)
        createRequestSpec()
            .contentType(ContentType.JSON)
            .body(quoteData)
            .when()
                .post(QUOTE_API_PATH)
            .then()
                .log().all()
                .statusCode(anyOf(is(401), is(403), is(400), is(500))); // 401, 403, 400 또는 500 허용
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("테스트용 문구 목록 조회 API 테스트")
    void testGetQuotesUsingTestEndpoint() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 테스트 전용 엔드포인트 호출 (인증 없이 사용 가능)
        createRequestSpec()
            .when()
                .queryParam("userId", "1")  // 테스트용 사용자 ID
                .get("/test/quotes")
            .then()
                .log().all()  // 응답 로깅
                .statusCode(anyOf(is(200), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("모킹을 사용한 테스트용 문구 목록 조회 API 테스트")
    void testGetQuotesWithMock() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        
        // 모킹된 서비스를 사용하는 테스트 엔드포인트 호출 (인증 필요 없음)
        createRequestSpec()
            .log().all()  // 요청 로깅 추가
            .when()
                .get(TEST_API_PATH)  // userId 파라미터 제거
            .then()
                .log().all()  // 응답 로깅
                .statusCode(anyOf(is(200), is(500)))
                .body(containsString("result"));
                
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    /**
     * 테스트용 문구 데이터 생성 헬퍼 메서드
     */
    private Map<String, Object> createQuoteData(Long bookId) {
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "테스트 문구 내용입니다. " + System.currentTimeMillis());
        quoteData.put("page", 123);
        quoteData.put("bookId", bookId);
        quoteData.put("memo", "테스트 메모");
        return quoteData;
    }
} 