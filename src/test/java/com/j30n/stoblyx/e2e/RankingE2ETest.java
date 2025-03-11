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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 게이미피케이션 및 랭킹 시스템 E2E 테스트 클래스
 */
@ExtendWith(E2ETestExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("게이미피케이션 및 랭킹 시스템 E2E 테스트")
@Tag("e2e")
public class RankingE2ETest extends BaseE2ETest {

    private TestUser testUser;
    private AuthTestHelper authHelper;
    private String createdQuoteId;
    private String createdContentId;
    
    @Override
    public void setUpAll() {
        super.setUpAll();
        testUser = TestDataGenerator.generateTestUser();
    }
    
    @BeforeEach
    public void setUp() {
        super.setUp();
        authHelper = new AuthTestHelper(createRequestSpec());
    }
    
    @Test
    @Order(1)
    @DisplayName("사용자 등록 및 로그인")
    public void testUserRegistrationAndLogin() {
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
    }
    
    @Test
    @Order(2)
    @DisplayName("명언 생성 테스트")
    public void testCreateQuote() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 명언 생성 요청
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "꿈을 이루고자 하는 용기만 있다면 모든 꿈을 이룰 수 있다.");
        quoteData.put("author", "월트 디즈니");
        quoteData.put("tags", new String[]{"꿈", "용기", "도전"});
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(quoteData)
            .when()
            .post("/quotes");
            
        assertThat("명언 생성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 생성된 명언 ID 저장
        createdQuoteId = response.path("data.id").toString();
        assertThat("명언 ID 존재", createdQuoteId, notNullValue());
    }
    
    @Test
    @Order(3)
    @DisplayName("콘텐츠 생성 테스트")
    public void testCreateContent() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 명언 ID가 없는 경우 먼저 생성
        if (createdQuoteId == null) {
            testCreateQuote();
        }
        
        // 콘텐츠 생성 요청
        Map<String, Object> contentData = new HashMap<>();
        contentData.put("quoteId", createdQuoteId);
        contentData.put("title", "꿈을 이루는 방법");
        contentData.put("type", "REFLECTION");
        contentData.put("tags", new String[]{"성공", "동기부여", "자기계발"});
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(contentData)
            .when()
            .post("/contents");
            
        // API 응답이 비동기일 경우를 고려
        assertThat("콘텐츠 생성 요청 성공", response.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        
        // 생성된 콘텐츠 ID가 있는 경우 저장
        if (response.path("data.id") != null) {
            createdContentId = response.path("data.id").toString();
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("활동 내역 조회 테스트")
    public void testGetActivities() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 콘텐츠 생성이 되어있지 않은 경우 먼저 생성
        if (createdContentId == null) {
            testCreateQuote();
            testCreateContent();
        }
        
        // 활동 내역 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/activities");
            
        assertThat("활동 내역 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(5)
    @DisplayName("뱃지 및 업적 조회 테스트")
    public void testGetAchievements() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 업적 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/achievements");
            
        assertThat("업적 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(6)
    @DisplayName("사용자 레벨 및 경험치 조회 테스트")
    public void testGetUserLevel() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 사용자 레벨 및 경험치 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/level");
            
        assertThat("사용자 레벨 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 레벨 및 경험치 정보 확인
        assertThat("레벨 정보 존재", response.path("data.level"), notNullValue());
        assertThat("경험치 정보 존재", response.path("data.experience"), notNullValue());
    }
    
    @Test
    @Order(7)
    @DisplayName("랭킹 리더보드 조회 테스트")
    public void testGetLeaderboard() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 랭킹 리더보드 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/rankings/leaderboard");
            
        assertThat("랭킹 리더보드 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 리더보드 데이터 확인
        assertThat("리더보드 데이터 존재", 
            response.path("data"), instanceOf(java.util.List.class));
    }
    
    @Test
    @Order(8)
    @DisplayName("일일 도전과제 조회 테스트")
    public void testGetDailyChallenges() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 일일 도전과제 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/challenges/daily");
            
        assertThat("일일 도전과제 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
} 