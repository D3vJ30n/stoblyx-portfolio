package com.j30n.stoblyx.e2e;

import com.j30n.stoblyx.e2e.util.AuthTestHelper;
import com.j30n.stoblyx.e2e.util.E2ETestExtension;
import com.j30n.stoblyx.e2e.util.TestDataGenerator;
import com.j30n.stoblyx.e2e.util.TestDataGenerator.TestUser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 책 및 숏폼 추천 시스템 E2E 테스트 클래스
 */
@ExtendWith(E2ETestExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("책 및 숏폼 추천 시스템 E2E 테스트")
@Tag("e2e")
class RecommendationE2ETest extends BaseE2ETest {

    private static final String USER_SIMILARITY_PATH = "/recommendations/user-similarity";
    
    private TestUser testUser;
    private TestUser secondUser;
    private AuthTestHelper authHelper;
    private AuthTestHelper secondAuthHelper;
    
    @Override
    public void setUpAll() {
        super.setUpAll();
        testUser = TestDataGenerator.generateTestUser();
        secondUser = TestDataGenerator.generateTestUser();
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        authHelper = new AuthTestHelper(createRequestSpec());
        secondAuthHelper = new AuthTestHelper(createRequestSpec());
    }
    
    @Test
    @Order(1)
    @DisplayName("사용자 등록 및 로그인")
    void testUserRegistrationAndLogin() {
        // 회원가입
        Response signUpResponse = authHelper.signUp(
            testUser.getUsername(),
            testUser.getEmail(),
            testUser.getPassword(),
            testUser.getNickname()
        );
        
        assertThat("회원가입 성공", signUpResponse.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        
        // 로그인
        Response loginResponse = authHelper.login(
            testUser.getUsername(),
            testUser.getPassword()
        );
        
        assertThat("로그인 성공", loginResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("액세스 토큰 존재", authHelper.getAccessToken(), notNullValue());
        
        // 두 번째 사용자 회원가입
        Response secondSignUpResponse = secondAuthHelper.signUp(
            secondUser.getUsername(),
            secondUser.getEmail(),
            secondUser.getPassword(),
            secondUser.getNickname()
        );
        
        assertThat("두 번째 사용자 회원가입 성공", secondSignUpResponse.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        
        // 두 번째 사용자 로그인
        Response secondLoginResponse = secondAuthHelper.login(
            secondUser.getUsername(),
            secondUser.getPassword()
        );
        
        assertThat("두 번째 사용자 로그인 성공", 
            secondLoginResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(2)
    @DisplayName("사용자 관심사 설정")
    void testSetUserInterests() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 첫 번째 사용자 관심사 설정
        Map<String, Object> interestsData = new HashMap<>();
        interestsData.put("interests", Arrays.asList("인공지능", "자기계발", "경영", "철학"));
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(interestsData)
            .when()
            .post("/users/interests");
            
        assertThat("관심사 설정 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 두 번째 사용자 관심사 설정 (유사성 테스트를 위해)
        Map<String, Object> secondInterestsData = new HashMap<>();
        secondInterestsData.put("interests", Arrays.asList("인공지능", "심리학", "소설", "역사"));
        
        Response secondResponse = createRequestSpec()
            .headers(secondAuthHelper.getAuthHeaders())
            .body(secondInterestsData)
            .when()
            .post("/users/interests");
            
        assertThat("두 번째 사용자 관심사 설정 성공", secondResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(3)
    @DisplayName("인기 검색어 조회 테스트")
    void testPopularSearchTerms() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 인기 검색어 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/popular-searches");
            
        assertThat("인기 검색어 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 인기 검색어 모델 데이터 검증
        if (response.path("data[0]") != null) {
            assertThat("검색어 존재", response.path("data[0].term"), notNullValue());
            assertThat("검색 횟수 존재", response.path("data[0].count"), notNullValue());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("책 검색 기록 생성 테스트")
    void testBookSearch() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 여러 검색어로 책 검색하여 검색 기록 생성
        for (String keyword : new String[] {"인공지능", "심리학", "경영", "소설"}) {
            Response response = createRequestSpec()
                .headers(authHelper.getAuthHeaders())
                .queryParam("keyword", keyword)
                .when()
                .get("/books/search");
                
            assertThat("책 검색 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
            assertThat("응답 결과 성공", response.path("result").toString(), 
                equalToIgnoringCase("success"));
        }
        
        // 두 번째 사용자도 검색 기록 생성
        for (String keyword : new String[] {"인공지능", "심리학", "철학", "역사"}) {
            Response response = createRequestSpec()
                .headers(secondAuthHelper.getAuthHeaders())
                .queryParam("keyword", keyword)
                .when()
                .get("/books/search");
                
            assertThat("두 번째 사용자 책 검색 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("검색 기록 조회 테스트")
    void testSearchHistory() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testBookSearch(); // 검색 기록이 없다면 먼저 생성
        }
        
        // 검색 기록 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/search/history");
            
        assertThat("검색 기록 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 검색 기록이 존재하는지 확인
        assertThat("검색 기록 존재", 
            response.path("data"), instanceOf(java.util.List.class));
    }
    
    @Test
    @Order(6)
    @DisplayName("검색어 프로필 조회 테스트")
    void testSearchTermProfile() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testBookSearch(); // 검색 기록이 없다면 먼저 생성
        }
        
        // 검색어 프로필 조회 요청 (사용자 패턴 분석)
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/search/profile");
            
        assertThat("검색어 프로필 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 검색어 성향 분석 결과가 존재하는지 확인
        if (response.path("data") != null) {
            assertThat("검색 성향 분석 존재", response.path("data.topCategories"), notNullValue());
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("추천 책 조회 테스트")
    void testRecommendedBooks() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testSetUserInterests();
            testBookSearch(); // 추천을 위한 검색 기록 생성
        }
        
        // 추천 책 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/recommended");
            
        assertThat("추천 책 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 추천 책 유형별 조회 (검색 기록 기반)
        Response historyBasedResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("recommendationType", "HISTORY_BASED")
            .when()
            .get("/books/recommended");
            
        assertThat("검색 기록 기반 추천 책 조회 성공", historyBasedResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 추천 책 유형별 조회 (관심사 기반)
        Response interestBasedResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("recommendationType", "INTEREST_BASED")
            .when()
            .get("/books/recommended");
            
        assertThat("관심사 기반 추천 책 조회 성공", interestBasedResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(8)
    @DisplayName("추천 숏폼 조회 테스트")
    void testRecommendedShortforms() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testSetUserInterests();
            testBookSearch(); // 추천을 위한 검색 기록 생성
        }
        
        // 추천 숏폼 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/shortforms/recommended");
            
        assertThat("추천 숏폼 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 추천 숏폼 유형별 조회 (인기도 기반)
        Response popularityBasedResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("recommendationType", "POPULARITY_BASED")
            .when()
            .get("/shortforms/recommended");
            
        assertThat("인기도 기반 추천 숏폼 조회 성공", popularityBasedResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(9)
    @DisplayName("트렌딩 숏폼 조회 테스트")
    void testTrendingShortforms() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 트렌딩 숏폼 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/shortforms/trending");
            
        assertThat("트렌딩 숏폼 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 트렌딩 숏폼 기간별 조회 (일간)
        Response dailyTrendingResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("period", "DAILY")
            .when()
            .get("/shortforms/trending");
            
        assertThat("일간 트렌딩 숏폼 조회 성공", dailyTrendingResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 트렌딩 숏폼 기간별 조회 (주간)
        Response weeklyTrendingResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("period", "WEEKLY")
            .when()
            .get("/shortforms/trending");
            
        assertThat("주간 트렌딩 숏폼 조회 성공", weeklyTrendingResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(10)
    @DisplayName("유사한 장르 책 추천 테스트")
    void testSimilarGenreBooks() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 책 검색 요청
        Response searchResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("keyword", "심리학")
            .when()
            .get("/books/search");
        
        // 첫 번째 검색 결과 ID 추출
        String bookId = searchResponse.path("data[0].id");
        
        // 유사한 장르 책 추천 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/" + bookId + "/similar");
            
        assertThat("유사한 장르 책 추천 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(11)
    @DisplayName("사용자 유사성 기반 추천 테스트")
    void testUserSimilarityRecommendations() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null || secondAuthHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testSetUserInterests();
            testBookSearch();
        }
        
        // 비슷한 취향의 사용자 추천 콘텐츠 조회
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_SIMILARITY_PATH);
            
        assertThat("사용자 유사성 기반 추천 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 사용자 유사성 추천 상세 조회 (책 추천)
        Response booksResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("contentType", "BOOK")
            .when()
            .get(USER_SIMILARITY_PATH);
            
        assertThat("사용자 유사성 기반 책 추천 조회 성공", booksResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 사용자 유사성 추천 상세 조회 (숏폼 추천)
        Response shortformsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("contentType", "SHORTFORM")
            .when()
            .get(USER_SIMILARITY_PATH);
            
        assertThat("사용자 유사성 기반 숏폼 추천 조회 성공", shortformsResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(12)
    @DisplayName("개인화된 주간 추천 테스트")
    void testPersonalizedWeeklyRecommendations() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testSetUserInterests();
            testBookSearch(); // 추천을 위한 검색 기록 생성
        }
        
        // 개인화된 주간 추천 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/recommendations/weekly");
            
        assertThat("개인화된 주간 추천 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 추천 데이터 검증
        if (response.path("data") != null) {
            assertThat("추천 콘텐츠 목록 존재", response.path("data.recommendedContent"), notNullValue());
            assertThat("추천 사유 존재", response.path("data.recommendationReason"), notNullValue());
        }
    }
} 