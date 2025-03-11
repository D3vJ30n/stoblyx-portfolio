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
 * 책 콘텐츠 및 미디어 E2E 테스트 클래스
 */
@ExtendWith(E2ETestExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("책 콘텐츠 및 미디어 E2E 테스트")
@Tag("e2e")
class ContentE2ETest extends BaseE2ETest {

    private TestUser testUser;
    private AuthTestHelper authHelper;
    private String bookId;
    private String contentId;
    private String shortformId;
    private String quoteId;
    private String mediaResourceId;
    
    @Override
    public void setUpAll() {
        super.setUpAll();
        testUser = TestDataGenerator.generateTestUser();
    }
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        authHelper = new AuthTestHelper(createRequestSpec());
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
    }
    
    @Test
    @Order(2)
    @DisplayName("사용자 관심사 설정")
    void testSetUserInterests() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 관심사 설정 요청
        Map<String, Object> interestsData = new HashMap<>();
        interestsData.put("interests", Arrays.asList("인공지능", "심리학", "자기계발", "소설"));
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(interestsData)
            .when()
            .post("/users/interests");
            
        assertThat("관심사 설정 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(3)
    @DisplayName("책 검색 테스트")
    void testBookSearch() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 책 검색 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("keyword", "인공지능")
            .when()
            .get("/books/search");
            
        assertThat("책 검색 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 검색 결과 존재 확인
        assertThat("검색 결과 존재", 
            response.path("data"), instanceOf(java.util.List.class));
            
        // 첫 번째 검색 결과 ID 저장 (이후 테스트에서 사용)
        if (response.path("data[0].id") != null) {
            bookId = response.path("data[0].id").toString();
            assertThat("책 ID 존재", bookId, notNullValue());
        }
        
        // 인기 검색어 확인
        Response popularResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/popular-searches");
            
        assertThat("인기 검색어 조회 성공", popularResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(4)
    @DisplayName("책 세부 정보 및 메타데이터 조회 테스트")
    void testGetBookDetails() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 책 ID가 없는 경우 검색 테스트 먼저 실행
        if (bookId == null) {
            testBookSearch();
        }
        
        // 책 세부 정보 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/" + bookId);
            
        assertThat("책 세부 정보 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 책 정보 검증
        assertThat("책 제목 존재", response.path("data.title"), notNullValue());
        assertThat("책 저자 존재", response.path("data.author"), notNullValue());
        assertThat("책 표지 이미지 URL 존재", response.path("data.coverImageUrl"), notNullValue());
        
        // 책 메타데이터 확인
        Response metadataResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/" + bookId + "/metadata");
            
        assertThat("책 메타데이터 조회 성공", metadataResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 책 인용구 조회
        Response quotesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/books/" + bookId + "/quotes");
            
        assertThat("책 인용구 조회 성공", quotesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 첫 번째 인용구 ID 저장 (이후 테스트에서 사용)
        if (quotesResponse.path("data[0].id") != null) {
            quoteId = quotesResponse.path("data[0].id").toString();
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("책 내용 요약 생성 테스트")
    void testGenerateBookSummary() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 책 ID가 없는 경우 검색 테스트 먼저 실행
        if (bookId == null) {
            testBookSearch();
        }
        
        // 책 내용 요약 생성 요청
        Map<String, Object> summaryData = new HashMap<>();
        summaryData.put("bookId", bookId);
        summaryData.put("summaryType", "SHORT"); // SHORT, MEDIUM, LONG
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(summaryData)
            .when()
            .post("/books/summary");
            
        assertThat("책 내용 요약 생성 성공", response.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 생성된 요약 내용 ID 저장
        contentId = response.path("data.id");
        assertThat("요약 내용 ID 존재", contentId, notNullValue());
    }
    
    @Test
    @Order(6)
    @DisplayName("사용자 랭크 및 제한 확인 테스트")
    void testUserRankAndLimits() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 사용자 랭크 조회
        Response rankResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/rank");
            
        assertThat("사용자 랭크 조회 성공", rankResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 사용자 콘텐츠 생성 제한 조회
        Response limitsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/content-limits");
            
        assertThat("콘텐츠 생성 제한 조회 성공", limitsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 남은 일일 콘텐츠 생성 횟수 확인
        int remainingCreations = limitsResponse.path("data.remainingCreations");
        assertThat("남은 콘텐츠 생성 횟수 존재", remainingCreations >= 0, is(true));
    }
    
    @Test
    @Order(7)
    @DisplayName("요약 내용 저장 테스트")
    void testSaveBookSummary() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 요약 내용 ID가 없는 경우 생성 테스트 먼저 실행
        if (contentId == null) {
            testGenerateBookSummary();
        }
        
        // 요약 내용 저장 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .post("/contents/" + contentId + "/save");
            
        assertThat("요약 내용 저장 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(8)
    @DisplayName("미디어 리소스 조회 테스트")
    void testGetMediaResources() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 책 ID가 없는 경우 검색 테스트 먼저 실행
        if (bookId == null) {
            testBookSearch();
        }
        
        // 책 관련 미디어 리소스 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("bookId", bookId)
            .when()
            .get("/media-resources");
            
        assertThat("미디어 리소스 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 미디어 리소스 ID 저장 (이후 테스트에서 사용)
        if (response.path("data[0].id") != null) {
            mediaResourceId = response.path("data[0].id").toString();
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("숏폼 콘텐츠 생성 테스트 - 자동 감정 분석")
    void testCreateShortformContentWithAutoEmotion() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 요약 내용 ID가 없는 경우 생성 테스트 먼저 실행
        if (contentId == null) {
            testGenerateBookSummary();
        }
        
        // 숏폼 콘텐츠 생성 요청 (자동 감정 분석)
        Map<String, Object> shortformData = new HashMap<>();
        shortformData.put("contentId", contentId);
        shortformData.put("title", "AI의 미래에 관한 통찰");
        shortformData.put("mediaType", "VIDEO"); // VIDEO, IMAGE
        shortformData.put("autoEmotionAnalysis", true); // 자동 감정 분석 활성화
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(shortformData)
            .when()
            .post("/shortforms");
            
        // API 응답이 비동기일 경우 고려
        assertThat("숏폼 콘텐츠 생성 요청 성공", response.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value()), is(HttpStatus.ACCEPTED.value())));
        
        // 생성된 숏폼 ID가 있는 경우 저장
        if (response.path("data.id") != null) {
            shortformId = response.path("data.id").toString();
            assertThat("숏폼 ID 존재", shortformId, notNullValue());
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("숏폼 콘텐츠 생성 테스트 - 수동 BGM 선택")
    void testCreateShortformContentWithManualBgm() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 요약 내용 ID가 없는 경우 생성 테스트 먼저 실행
        if (contentId == null) {
            testGenerateBookSummary();
        }
        
        // 콘텐츠 생성 제한 확인
        Response limitsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/content-limits");
            
        int remainingCreations = limitsResponse.path("data.remainingCreations");
        
        // 남은 생성 횟수가 있는 경우에만 테스트 진행
        if (remainingCreations > 0) {
            // 숏폼 콘텐츠 생성 요청 (수동 BGM 선택)
            Map<String, Object> shortformData = new HashMap<>();
            shortformData.put("contentId", contentId);
            shortformData.put("title", "심리학 이론의 실제 적용");
            shortformData.put("mediaType", "IMAGE"); // VIDEO, IMAGE
            shortformData.put("autoEmotionAnalysis", false); // 자동 감정 분석 비활성화
            shortformData.put("bgmEmotion", "CALM"); // CALM, HAPPY, NEUTRAL, SAD
            
            if (mediaResourceId != null) {
                shortformData.put("mediaResourceId", mediaResourceId);
            }
            
            Response response = createRequestSpec()
                .headers(authHelper.getAuthHeaders())
                .body(shortformData)
                .when()
                .post("/shortforms");
                
            // API 응답이 비동기일 경우 고려
            assertThat("숏폼 콘텐츠 생성 요청 성공", response.getStatusCode(), 
                anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value()), is(HttpStatus.ACCEPTED.value())));
        }
    }
    
    @Test
    @Order(11)
    @DisplayName("숏폼 조회 테스트")
    void testGetShortform() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 숏폼 ID가 없는 경우 생성 테스트 먼저 실행
        if (shortformId == null) {
            testCreateShortformContentWithAutoEmotion();
        }
        
        // 숏폼 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/shortforms/" + shortformId);
            
        assertThat("숏폼 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 숏폼 정보 검증
        assertThat("숏폼 제목 존재", response.path("data.title"), notNullValue());
        assertThat("미디어 URL 존재", response.path("data.mediaUrl"), notNullValue());
        assertThat("자막 존재", response.path("data.subtitles"), notNullValue());
        assertThat("BGM 정보 존재", response.path("data.bgmInfo"), notNullValue());
        assertThat("BGM 감정 타입 존재", response.path("data.bgmInfo.emotionType"), notNullValue());
    }
    
    @Test
    @Order(12)
    @DisplayName("인용구 좋아요 테스트")
    void testLikeQuote() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 인용구 ID가 없는 경우 책 세부정보 조회 테스트 먼저 실행
        if (quoteId == null) {
            testGetBookDetails();
            
            // 인용구 ID가 여전히 없는 경우 직접 조회
            if (quoteId == null) {
                Response quotesResponse = createRequestSpec()
                    .headers(authHelper.getAuthHeaders())
                    .when()
                    .get("/books/" + bookId + "/quotes");
                
                if (quotesResponse.path("data[0].id") != null) {
                    quoteId = quotesResponse.path("data[0].id").toString();
                } else {
                    // 테스트 스킵 (인용구가 없는 경우)
                    return;
                }
            }
        }
        
        // 인용구 좋아요 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .post("/quotes/" + quoteId + "/like");
            
        assertThat("인용구 좋아요 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(13)
    @DisplayName("숏폼 좋아요 테스트")
    void testLikeShortform() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 숏폼 ID가 없는 경우 생성 테스트 먼저 실행
        if (shortformId == null) {
            testCreateShortformContentWithAutoEmotion();
        }
        
        // 숏폼 좋아요 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .post("/shortforms/" + shortformId + "/like");
            
        assertThat("숏폼 좋아요 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(14)
    @DisplayName("숏폼 공유 테스트")
    void testShareShortform() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 숏폼 ID가 없는 경우 생성 테스트 먼저 실행
        if (shortformId == null) {
            testCreateShortformContentWithAutoEmotion();
        }
        
        // 숏폼 공유 요청
        Map<String, Object> shareData = new HashMap<>();
        shareData.put("platform", "KAKAO"); // KAKAO, FACEBOOK, TWITTER
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(shareData)
            .when()
            .post("/shortforms/" + shortformId + "/share");
            
        assertThat("숏폼 공유 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(15)
    @DisplayName("숏폼 저장 및 나중에 보기 테스트")
    void testSaveShortformForLater() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 숏폼 ID가 없는 경우 생성 테스트 먼저 실행
        if (shortformId == null) {
            testCreateShortformContentWithAutoEmotion();
        }
        
        // 숏폼 저장 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .post("/shortforms/" + shortformId + "/bookmark");
            
        assertThat("숏폼 저장 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 저장된 숏폼 목록 조회
        Response savedResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/bookmarks/shortforms");
            
        assertThat("저장된 숏폼 목록 조회 성공", savedResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
    
    @Test
    @Order(16)
    @DisplayName("숏폼 댓글 작성 테스트")
    void testAddCommentToShortform() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 숏폼 ID가 없는 경우 생성 테스트 먼저 실행
        if (shortformId == null) {
            testCreateShortformContentWithAutoEmotion();
        }
        
        // 댓글 작성 요청
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("shortformId", shortformId);
        commentData.put("text", "정말 인상적인 요약이네요. 책을 읽고 싶어졌습니다!");
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(commentData)
            .when()
            .post("/comments");
            
        assertThat("댓글 작성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(17)
    @DisplayName("사용자 활동 내역 및 뱃지 확인 테스트")
    void testUserActivityAndBadges() {
        // 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 사용자 활동 내역 조회
        Response activitiesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/activities");
            
        assertThat("사용자 활동 내역 조회 성공", activitiesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 사용자 뱃지 조회
        Response badgesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/badges");
            
        assertThat("사용자 뱃지 조회 성공", badgesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 사용자 업적 조회
        Response achievementsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/achievements");
            
        assertThat("사용자 업적 조회 성공", achievementsResponse.getStatusCode(), is(HttpStatus.OK.value()));
    }
} 