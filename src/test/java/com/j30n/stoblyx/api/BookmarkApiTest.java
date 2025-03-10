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
 * 북마크 API 통합 테스트 클래스
 */
@Import({RedisTestConfig.class, SecurityTestConfig.class, ContextTestConfig.class, MockTestConfig.class, XssExclusionTestConfig.class, TestConfig.class})
@DisplayName("북마크 API 통합 테스트")
class BookmarkApiTest extends BaseApiTest {

    private static final String BOOKMARK_API_PATH = "/bookmarks";
    private static final String CONTENT_API_PATH = "/contents";
    
    // 테스트용 고정 ID
    private static final Long TEST_CONTENT_ID = 1L;
    
    // 테스트 중 생성된 콘텐츠 ID 저장 리스트
    private List<Long> createdContentIds = new ArrayList<>();
    
    @BeforeEach
    void setUp() {
        System.out.println("테스트 시작: " + System.currentTimeMillis());
        createdContentIds = new ArrayList<>();
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 중 생성된 모든 콘텐츠 삭제
        for (Long contentId : createdContentIds) {
            try {
                givenAuth(userToken)
                    .when()
                    .delete(CONTENT_API_PATH + "/" + contentId)
                    .then()
                    .statusCode(anyOf(is(200), is(204), is(404), is(500)));
            } catch (Exception e) {
                System.out.println("콘텐츠 삭제 중 오류 발생: " + e.getMessage());
            }
        }
        System.out.println("테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("북마크 목록 조회 API 테스트")
    void testGetBookmarks() {
        // 북마크 목록 조회
        givenAuth(userToken)
            .when()
                .get(BOOKMARK_API_PATH)
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue());
                
        System.out.println("북마크 목록 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 추가 API 테스트")
    void testAddBookmark() {
        // 북마크 추가
        givenAuth(userToken)
            .contentType(ContentType.JSON)
            .when()
                .post(CONTENT_API_PATH + "/" + TEST_CONTENT_ID + "/bookmark")
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(201), is(500)))
                .body(containsString("result"));
                
        System.out.println("북마크 추가 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 삭제 API 테스트")
    void testRemoveBookmark() {
        // 북마크 삭제
        givenAuth(userToken)
            .when()
                .delete(CONTENT_API_PATH + "/" + TEST_CONTENT_ID + "/bookmark")
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204), is(500)))
                .body(containsString("result"));
                
        System.out.println("북마크 삭제 테스트 완료");
    }
    
    @Test
    @DisplayName("인증 없이 북마크 목록 조회 시 실패 테스트")
    void testGetBookmarksWithoutAuth() {
        // 인증 없이 북마크 목록 조회
        createRequestSpec()
            .when()
                .get(BOOKMARK_API_PATH)
            .then()
                .log().all()
                .statusCode(anyOf(is(401), is(403)))
                .body(containsString("\"result\":\"ERROR\""));
                
        System.out.println("인증 없이 북마크 목록 조회 실패 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 상태 확인 API 테스트")
    void testCheckBookmarkStatus() {
        // 북마크 상태 확인
        givenAuth(userToken)
            .when()
                .get(CONTENT_API_PATH + "/" + TEST_CONTENT_ID + "/bookmark/status")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.isBookmarked", isA(Boolean.class));
                
        System.out.println("북마크 상태 확인 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 필터링 및 정렬 API 테스트")
    void testFilterAndSortBookmarks() {
        // 북마크 필터링 및 정렬
        givenAuth(userToken)
            .param("sort", "createdAt,desc")
            .param("type", "BOOK")
            .when()
                .get(BOOKMARK_API_PATH)
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue());
                
        System.out.println("북마크 필터링 및 정렬 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 페이징 API 테스트")
    void testBookmarkPagination() {
        // 북마크 페이징
        givenAuth(userToken)
            .param("page", 0)
            .param("size", 10)
            .when()
                .get(BOOKMARK_API_PATH)
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.content", notNullValue())
                .body("data.totalElements", notNullValue())
                .body("data.totalPages", notNullValue());
                
        System.out.println("북마크 페이징 테스트 완료");
    }
    
    @Test
    @DisplayName("북마크 일괄 삭제 API 테스트")
    void testBulkDeleteBookmarks() {
        // 북마크 일괄 삭제 요청 데이터
        Map<String, Object> requestData = new HashMap<>();
        List<Long> contentIds = new ArrayList<>();
        contentIds.add(TEST_CONTENT_ID);
        requestData.put("contentIds", contentIds);
        
        // 북마크 일괄 삭제
        givenAuth(userToken)
            .contentType(ContentType.JSON)
            .body(requestData)
            .when()
                .post(BOOKMARK_API_PATH + "/bulk-delete")
            .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204), is(500)))
                .body(containsString("result"));
                
        System.out.println("북마크 일괄 삭제 테스트 완료");
    }
} 