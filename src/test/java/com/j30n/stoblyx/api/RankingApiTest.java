package com.j30n.stoblyx.api;

import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MockTestConfig;
import com.j30n.stoblyx.config.RedisTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.TestConfig;
import com.j30n.stoblyx.config.XssExclusionTestConfig;
import com.j30n.stoblyx.domain.enums.RankType;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * 랭킹 API 통합 테스트 클래스
 */
@Import({RedisTestConfig.class, SecurityTestConfig.class, ContextTestConfig.class, MockTestConfig.class, XssExclusionTestConfig.class, TestConfig.class})
@DisplayName("랭킹 API 통합 테스트")
class RankingApiTest extends BaseApiTest {

    private static final String RANKING_API_PATH = "/ranking";
    private static final String USER_API_PATH = "/users";
    
    @BeforeEach
    void setUp() {
        System.out.println("랭킹 API 테스트 시작: " + System.currentTimeMillis());
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("랭킹 API 테스트 종료: " + System.currentTimeMillis());
    }

    @Test
    @DisplayName("상위 사용자 랭킹 조회 API 테스트")
    void testGetTopUsers() {
        // 상위 사용자 랭킹 조회
        givenAuth(userToken)
            .param("limit", 10)
            .when()
                .get(RANKING_API_PATH + "/top")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data", hasSize(lessThanOrEqualTo(10)))
                .body("data[0].userId", notNullValue())
                .body("data[0].score", notNullValue())
                .body("data[0].rankType", notNullValue());
                
        System.out.println("상위 사용자 랭킹 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("사용자 랭킹 정보 조회 API 테스트")
    void testGetUserRanking() {
        // 현재 사용자의 랭킹 정보 조회
        givenAuth(userToken)
            .when()
                .get(USER_API_PATH + "/me/ranking")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.userId", notNullValue())
                .body("data.score", notNullValue())
                .body("data.rankType", notNullValue())
                .body("data.rank", notNullValue());
                
        System.out.println("사용자 랭킹 정보 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("랭크 타입별 사용자 목록 조회 API 테스트")
    void testGetUsersByRankType() {
        // 골드 랭크 사용자 목록 조회
        givenAuth(userToken)
            .param("rankType", RankType.GOLD.name())
            .param("page", 0)
            .param("size", 10)
            .when()
                .get(RANKING_API_PATH + "/users")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.content", notNullValue())
                .body("data.totalElements", notNullValue())
                .body("data.totalPages", notNullValue());
                
        System.out.println("랭크 타입별 사용자 목록 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("랭킹 통계 조회 API 테스트")
    void testGetRankingStatistics() {
        // 랭킹 통계 조회
        givenAuth(userToken)
            .when()
                .get(RANKING_API_PATH + "/statistics")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.rankDistribution", notNullValue())
                .body("data.averageScore", notNullValue());
                
        System.out.println("랭킹 통계 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("인증 없이 랭킹 조회 테스트")
    void testGetRankingWithoutAuth() {
        // 인증 없이 상위 사용자 랭킹 조회 (공개 API)
        createRequestSpec()
            .param("limit", 10)
            .when()
                .get(RANKING_API_PATH + "/top")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue());
                
        System.out.println("인증 없이 랭킹 조회 테스트 완료");
    }
    
    @Test
    @DisplayName("인증 없이 사용자 랭킹 정보 조회 시 실패 테스트")
    void testGetUserRankingWithoutAuth() {
        // 인증 없이 사용자 랭킹 정보 조회
        createRequestSpec()
            .when()
                .get(USER_API_PATH + "/me/ranking")
            .then()
                .log().all()
                .statusCode(anyOf(is(401), is(403)))
                .body(containsString("\"result\":\"ERROR\""));
                
        System.out.println("인증 없이 사용자 랭킹 정보 조회 실패 테스트 완료");
    }
    
    @Test
    @DisplayName("활동 점수 업데이트 API 테스트")
    void testUpdateActivityScore() {
        // 활동 점수 업데이트 요청 데이터
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("activityType", "CONTENT_CREATE");
        requestData.put("score", 10);
        
        // 활동 점수 업데이트
        givenAuth(userToken)
            .contentType(ContentType.JSON)
            .body(requestData)
            .when()
                .post(RANKING_API_PATH + "/activity")
            .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.userId", notNullValue())
                .body("data.score", notNullValue())
                .body("data.rankType", notNullValue());
                
        System.out.println("활동 점수 업데이트 테스트 완료");
    }
} 