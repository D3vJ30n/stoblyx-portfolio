package com.j30n.stoblyx.e2e;

import com.j30n.stoblyx.e2e.util.AuthTestHelper;
import com.j30n.stoblyx.e2e.util.E2ETestExtension;
import com.j30n.stoblyx.e2e.util.TestDataGenerator;
import com.j30n.stoblyx.e2e.util.TestDataGenerator.TestUser;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 전체 사용자 여정을 테스트하는 E2E 테스트 클래스
 * 회원가입부터 콘텐츠 생성, 상호작용, 랭킹 시스템까지 전체 흐름을 테스트합니다.
 */
@ExtendWith(E2ETestExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("사용자 여정 E2E 테스트")
@Tag("e2e")
class UserJourneyE2ETest extends BaseE2ETest {

    private static final String API_PATH = "/api";
    private static final String USER_API_PATH = API_PATH + "/user";
    private static final String BOOK_API_PATH = API_PATH + "/book";
    private static final String SEARCH_API_PATH = API_PATH + "/search";
    private static final String CONTENT_API_PATH = API_PATH + "/content";
    private static final String QUOTE_API_PATH = API_PATH + "/quote";
    private static final String COMMUNITY_API_PATH = API_PATH + "/community";
    private static final String RANKING_API_PATH = API_PATH + "/ranking";
    private static final String RECOMMENDATION_API_PATH = API_PATH + "/recommendation";
    
    private TestUser testUser;
    private AuthTestHelper authHelper;
    private Long bookId;
    private Long quoteId;
    private Long contentId;
    private Long postId;
    
    @Override
    public void setUpAll() {
        super.setUpAll();
        testUser = TestDataGenerator.generateTestUser();
    }
    
    @Override
    public void setUp() {
        super.setUp();
        authHelper = new AuthTestHelper(createRequestSpec());
    }
    
    @Test
    @Order(1)
    @DisplayName("1. 회원가입 및 로그인")
    void testSignUpAndLogin() {
        // 1.1 회원가입
        Response signUpResponse = authHelper.signUp(
            testUser.getUsername(),
            testUser.getEmail(),
            testUser.getPassword(),
            testUser.getNickname()
        );
        
        assertThat("회원가입 응답 코드", signUpResponse.getStatusCode(), 
                anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        assertThat("회원가입 응답 결과", signUpResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 1.2 로그인
        Response loginResponse = authHelper.login(
            testUser.getUsername(),
            testUser.getPassword()
        );
        
        assertThat("로그인 응답 코드", loginResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("로그인 응답 결과", loginResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        assertThat("액세스 토큰", loginResponse.path("data.accessToken"), notNullValue());
        
        // 1.3 사용자 관심사 설정
        Map<String, Object> interestRequest = new HashMap<>();
        List<String> interests = Arrays.asList("소설", "자기계발", "과학", "역사");
        interestRequest.put("interests", interests);
        
        Response interestResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(interestRequest)
            .when()
            .post(USER_API_PATH + "/interests");
            
        assertThat("관심사 설정 응답 코드", interestResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("관심사 설정 응답 결과", interestResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(2)
    @DisplayName("2. 검색 기능")
    void testSearch() {
        // 2.1 인기 검색어 조회
        Response popularTermsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(SEARCH_API_PATH + "/popular-terms");
            
        assertThat("인기 검색어 응답 코드", popularTermsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("인기 검색어 응답 결과", popularTermsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 2.2 책 검색
        String searchTerm = "초역 부처의 말";
        Response searchResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("query", searchTerm)
            .when()
            .get(SEARCH_API_PATH);
            
        assertThat("검색 응답 코드", searchResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("검색 응답 결과", searchResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 검색 결과가 있는지 확인하고 첫 번째 책 ID 저장
        List<Map<String, Object>> books = searchResponse.path("data.books");
        assertThat("검색 결과 책 목록", books, not(empty()));
        
        bookId = Long.valueOf(books.get(0).get("id").toString());
        assertThat("책 ID", bookId, notNullValue());
    }
    
    @Test
    @Order(3)
    @DisplayName("3. 검색 결과 필터링 및 정렬")
    void testSearchFiltersAndSorting() {
        // 3.1 장르별 필터링
        Response genreFilterResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("query", "소설")
            .queryParam("genre", "판타지")
            .when()
            .get(SEARCH_API_PATH);
            
        assertThat("장르 필터링 응답 코드", genreFilterResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("장르 필터링 응답 결과", genreFilterResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 3.2 출판일 기준 정렬
        Response sortByDateResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("query", "소설")
            .queryParam("sort", "publishDate")
            .queryParam("order", "desc")
            .when()
            .get(SEARCH_API_PATH);
            
        assertThat("출판일 정렬 응답 코드", sortByDateResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("출판일 정렬 응답 결과", sortByDateResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(4)
    @DisplayName("4. 책 상세 정보 조회")
    void testBookDetail() {
        // 4.1 책 상세 정보 조회
        Response bookDetailResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId);
            
        assertThat("책 상세 정보 응답 코드", bookDetailResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("책 상세 정보 응답 결과", bookDetailResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 4.2 책 요약 조회
        Response summaryResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId + "/summary");
            
        assertThat("책 요약 응답 코드", summaryResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("책 요약 응답 결과", summaryResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 4.3 책 인용구 목록 조회
        Response quotesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId + "/quotes");
            
        assertThat("인용구 목록 응답 코드", quotesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("인용구 목록 응답 결과", quotesResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 인용구가 있는지 확인하고 첫 번째 인용구 ID 저장
        List<Map<String, Object>> quotes = quotesResponse.path("data.quotes");
        if (!quotes.isEmpty()) {
            quoteId = Long.valueOf(quotes.get(0).get("id").toString());
        } else {
            // 인용구가 없는 경우 테스트용 인용구 생성
            Map<String, Object> quoteRequest = new HashMap<>();
            quoteRequest.put("bookId", bookId);
            quoteRequest.put("content", "지식이란 알면 알수록 더 많이 알아야 함을 깨닫는 것이다.");
            quoteRequest.put("page", 42);
            
            Response createQuoteResponse = createRequestSpec()
                .headers(authHelper.getAuthHeaders())
                .contentType(ContentType.JSON)
                .body(quoteRequest)
                .when()
                .post(QUOTE_API_PATH);
                
            assertThat("인용구 생성 응답 코드", createQuoteResponse.getStatusCode(), 
                    anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
            
            quoteId = createQuoteResponse.path("data.id");
        }
        
        assertThat("인용구 ID", quoteId, notNullValue());
    }
    
    @Test
    @Order(5)
    @DisplayName("5. 콘텐츠 보기")
    void testViewContent() {
        // 5.1 책 관련 콘텐츠 조회
        Response contentResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId + "/contents");
            
        assertThat("콘텐츠 응답 코드", contentResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("콘텐츠 응답 결과", contentResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 5.2 미디어 리소스 조회
        Response mediaResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId + "/media");
            
        assertThat("미디어 응답 코드", mediaResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("미디어 응답 결과", mediaResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 5.3 인용구 AI 요약 조회
        Response quoteSummaryResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(QUOTE_API_PATH + "/" + quoteId + "/summary");
            
        assertThat("인용구 요약 응답 코드", quoteSummaryResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("인용구 요약 응답 결과", quoteSummaryResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 5.4 짧은 형태의 콘텐츠 조회
        Response shortFormResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(BOOK_API_PATH + "/" + bookId + "/short-form");
            
        assertThat("짧은 형태 콘텐츠 응답 코드", shortFormResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("짧은 형태 콘텐츠 응답 결과", shortFormResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 콘텐츠가 있는지 확인하고 첫 번째 콘텐츠 ID 저장
        List<Map<String, Object>> contents = shortFormResponse.path("data.contents");
        if (!contents.isEmpty()) {
            contentId = Long.valueOf(contents.get(0).get("id").toString());
        } else {
            // 콘텐츠가 없는 경우 테스트용 콘텐츠 생성은 다음 테스트에서 수행
            contentId = null;
        }
        
        // 5.5 콘텐츠 상호작용 기록
        if (contentId != null) {
            Map<String, Object> interactionRequest = new HashMap<>();
            interactionRequest.put("contentId", contentId);
            interactionRequest.put("interactionType", "VIEW");
            
            Response interactionResponse = createRequestSpec()
                .headers(authHelper.getAuthHeaders())
                .contentType(ContentType.JSON)
                .body(interactionRequest)
                .when()
                .post(CONTENT_API_PATH + "/interaction");
                
            assertThat("상호작용 응답 코드", interactionResponse.getStatusCode(), is(HttpStatus.OK.value()));
            assertThat("상호작용 응답 결과", interactionResponse.path("result").toString(), 
                    equalToIgnoringCase("success"));
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("6. 사용자 상호작용")
    void testUserInteraction() {
        // 콘텐츠 ID가 없는 경우 콘텐츠 생성
        if (contentId == null) {
            Map<String, Object> contentRequest = new HashMap<>();
            contentRequest.put("bookId", bookId);
            contentRequest.put("title", "테스트 콘텐츠");
            contentRequest.put("content", "이것은 테스트를 위한 짧은 형태의 콘텐츠입니다.");
            contentRequest.put("emotionType", "HAPPY");
            
            Response createContentResponse = createRequestSpec()
                .headers(authHelper.getAuthHeaders())
                .contentType(ContentType.JSON)
                .body(contentRequest)
                .when()
                .post(CONTENT_API_PATH + "/short-form");
                
            assertThat("콘텐츠 생성 응답 코드", createContentResponse.getStatusCode(), 
                    anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
            
            contentId = createContentResponse.path("data.id");
            assertThat("콘텐츠 ID", contentId, notNullValue());
        }
        
        // 6.1 콘텐츠 좋아요
        Map<String, Object> likeRequest = new HashMap<>();
        likeRequest.put("contentId", contentId);
        
        Response likeResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(likeRequest)
            .when()
            .post(CONTENT_API_PATH + "/like");
            
        assertThat("좋아요 응답 코드", likeResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("좋아요 응답 결과", likeResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 6.2 콘텐츠 북마크
        Map<String, Object> bookmarkRequest = new HashMap<>();
        bookmarkRequest.put("contentId", contentId);
        
        Response bookmarkResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(bookmarkRequest)
            .when()
            .post(CONTENT_API_PATH + "/bookmark");
            
        assertThat("북마크 응답 코드", bookmarkResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("북마크 응답 결과", bookmarkResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 6.3 콘텐츠 댓글
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("contentId", contentId);
        commentRequest.put("text", "정말 좋은 콘텐츠입니다!");
        
        Response commentResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(commentRequest)
            .when()
            .post(CONTENT_API_PATH + "/comment");
            
        assertThat("댓글 응답 코드", commentResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("댓글 응답 결과", commentResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 6.4 인용구 저장
        Map<String, Object> saveQuoteRequest = new HashMap<>();
        saveQuoteRequest.put("quoteId", quoteId);
        
        Response saveQuoteResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(saveQuoteRequest)
            .when()
            .post(QUOTE_API_PATH + "/save");
            
        assertThat("인용구 저장 응답 코드", saveQuoteResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("인용구 저장 응답 결과", saveQuoteResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 6.5 인용구 좋아요
        Map<String, Object> quoteLikeRequest = new HashMap<>();
        quoteLikeRequest.put("quoteId", quoteId);
        
        Response quoteLikeResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(quoteLikeRequest)
            .when()
            .post(QUOTE_API_PATH + "/like");
            
        assertThat("인용구 좋아요 응답 코드", quoteLikeResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("인용구 좋아요 응답 결과", quoteLikeResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(7)
    @DisplayName("7. 커뮤니티 기능")
    void testCommunity() {
        // 7.1 커뮤니티 게시물 목록 조회
        Response postsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(COMMUNITY_API_PATH + "/posts");
            
        assertThat("게시물 목록 응답 코드", postsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("게시물 목록 응답 결과", postsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 7.2 게시물 작성
        Map<String, Object> postRequest = new HashMap<>();
        postRequest.put("title", "책 추천합니다");
        postRequest.put("content", "이 책은 정말 좋은 책입니다. 모두에게 추천합니다.");
        postRequest.put("bookId", bookId);
        List<String> tags = Arrays.asList("추천", "독서", "자기계발");
        postRequest.put("tags", tags);
        
        Response createPostResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(postRequest)
            .when()
            .post(COMMUNITY_API_PATH + "/posts");
            
        assertThat("게시물 작성 응답 코드", createPostResponse.getStatusCode(), 
                anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        assertThat("게시물 작성 응답 결과", createPostResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        postId = createPostResponse.path("data.id");
        assertThat("게시물 ID", postId, notNullValue());
        
        // 7.3 게시물 좋아요
        Map<String, Object> postLikeRequest = new HashMap<>();
        postLikeRequest.put("postId", postId);
        
        Response postLikeResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(postLikeRequest)
            .when()
            .post(COMMUNITY_API_PATH + "/posts/like");
            
        assertThat("게시물 좋아요 응답 코드", postLikeResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("게시물 좋아요 응답 결과", postLikeResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 7.4 게시물 댓글 작성
        Map<String, Object> postCommentRequest = new HashMap<>();
        postCommentRequest.put("postId", postId);
        postCommentRequest.put("content", "저도 이 책 읽어봤는데 정말 좋았어요!");
        
        Response postCommentResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(postCommentRequest)
            .when()
            .post(COMMUNITY_API_PATH + "/posts/comment");
            
        assertThat("게시물 댓글 응답 코드", postCommentResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("게시물 댓글 응답 결과", postCommentResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 7.5 태그별 게시물 필터링
        Response tagFilterResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .queryParam("tag", "추천")
            .when()
            .get(COMMUNITY_API_PATH + "/posts");
            
        assertThat("태그 필터링 응답 코드", tagFilterResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("태그 필터링 응답 결과", tagFilterResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(8)
    @DisplayName("8. 콘텐츠 생성")
    void testContentCreation() {
        // 8.1 사용자 랭크 확인
        Response rankResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/user-rank");
            
        assertThat("랭크 확인 응답 코드", rankResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("랭크 확인 응답 결과", rankResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 8.2 콘텐츠 생성 가능 횟수 확인
        Response limitResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(CONTENT_API_PATH + "/creation-limit");
            
        assertThat("생성 가능 횟수 응답 코드", limitResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("생성 가능 횟수 응답 결과", limitResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 8.3 숏폼 콘텐츠 생성
        Map<String, Object> createContentRequest = new HashMap<>();
        createContentRequest.put("bookId", bookId);
        createContentRequest.put("title", "책의 핵심 메시지");
        createContentRequest.put("content", "이 책의 핵심 메시지는 꾸준한 노력이 중요하다는 것입니다.");
        createContentRequest.put("emotionType", "HAPPY");
        createContentRequest.put("autoEmotionAnalysis", false);
        
        Response createContentResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(createContentRequest)
            .when()
            .post(CONTENT_API_PATH + "/create");
            
        assertThat("콘텐츠 생성 응답 코드", createContentResponse.getStatusCode(), 
                anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        assertThat("콘텐츠 생성 응답 결과", createContentResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 8.4 콘텐츠 생성 상태 확인
        Long contentCreationId = createContentResponse.path("data.id");
        assertThat("콘텐츠 생성 ID", contentCreationId, notNullValue());
        
        Response statusResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(CONTENT_API_PATH + "/status/" + contentCreationId);
            
        assertThat("콘텐츠 생성 상태 응답 코드", statusResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("콘텐츠 생성 상태 응답 결과", statusResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(9)
    @DisplayName("9. 추천 기능")
    void testRecommendation() {
        // 9.1 맞춤형 책 추천
        Response bookRecommendationResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RECOMMENDATION_API_PATH + "/books");
            
        assertThat("책 추천 응답 코드", bookRecommendationResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("책 추천 응답 결과", bookRecommendationResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 9.2 맞춤형 콘텐츠 추천
        Response contentRecommendationResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RECOMMENDATION_API_PATH + "/contents");
            
        assertThat("콘텐츠 추천 응답 코드", contentRecommendationResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("콘텐츠 추천 응답 결과", contentRecommendationResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 9.3 유사 사용자 기반 추천
        Response similarUserRecommendationResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RECOMMENDATION_API_PATH + "/similar-users");
            
        assertThat("유사 사용자 추천 응답 코드", similarUserRecommendationResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("유사 사용자 추천 응답 결과", similarUserRecommendationResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 9.4 트렌드 콘텐츠
        Response trendingResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RECOMMENDATION_API_PATH + "/trending");
            
        assertThat("트렌드 콘텐츠 응답 코드", trendingResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("트렌드 콘텐츠 응답 결과", trendingResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(10)
    @DisplayName("10. 게이미피케이션 및 랭킹 시스템")
    void testGamificationAndRanking() {
        // 10.1 사용자 랭크 및 점수 확인
        Response userScoreResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/user-score");
            
        assertThat("사용자 점수 응답 코드", userScoreResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("사용자 점수 응답 결과", userScoreResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 10.2 획득한 뱃지 목록 확인
        Response badgesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/badges");
            
        assertThat("뱃지 목록 응답 코드", badgesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("뱃지 목록 응답 결과", badgesResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 10.3 리더보드 확인
        Response leaderboardResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/leaderboard");
            
        assertThat("리더보드 응답 코드", leaderboardResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("리더보드 응답 결과", leaderboardResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 10.4 업적 목록 확인
        Response achievementsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/achievements");
            
        assertThat("업적 목록 응답 코드", achievementsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("업적 목록 응답 결과", achievementsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 10.5 랭크별 혜택 확인
        Response rankBenefitsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(RANKING_API_PATH + "/rank-benefits");
            
        assertThat("랭크 혜택 응답 코드", rankBenefitsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("랭크 혜택 응답 결과", rankBenefitsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(11)
    @DisplayName("11. 설정 및 관리")
    void testSettingsAndManagement() {
        // 11.1 사용자 프로필 설정 변경
        Map<String, Object> profileUpdateRequest = new HashMap<>();
        profileUpdateRequest.put("nickname", "새로운_" + testUser.getNickname());
        profileUpdateRequest.put("bio", "독서를 좋아하는 개발자입니다.");
        
        Response profileUpdateResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(profileUpdateRequest)
            .when()
            .put(USER_API_PATH + "/profile");
            
        assertThat("프로필 설정 응답 코드", profileUpdateResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("프로필 설정 응답 결과", profileUpdateResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 11.2 알림 설정 변경
        Map<String, Object> notificationSettingsRequest = new HashMap<>();
        notificationSettingsRequest.put("emailNotifications", true);
        notificationSettingsRequest.put("pushNotifications", true);
        notificationSettingsRequest.put("contentCreationNotifications", true);
        notificationSettingsRequest.put("commentNotifications", true);
        notificationSettingsRequest.put("likeNotifications", false);
        
        Response notificationSettingsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(notificationSettingsRequest)
            .when()
            .put(USER_API_PATH + "/notification-settings");
            
        assertThat("알림 설정 응답 코드", notificationSettingsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("알림 설정 응답 결과", notificationSettingsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 11.3 테마 설정 변경
        Map<String, Object> themeSettingsRequest = new HashMap<>();
        themeSettingsRequest.put("theme", "DARK");
        themeSettingsRequest.put("fontSize", "MEDIUM");
        
        Response themeSettingsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .contentType(ContentType.JSON)
            .body(themeSettingsRequest)
            .when()
            .put(USER_API_PATH + "/theme-settings");
            
        assertThat("테마 설정 응답 코드", themeSettingsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("테마 설정 응답 결과", themeSettingsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 11.4 사용자 활동 내역 조회
        Response userActivityResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_API_PATH + "/activities");
            
        assertThat("사용자 활동 응답 코드", userActivityResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("사용자 활동 응답 결과", userActivityResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(12)
    @DisplayName("12. 전체 사용자 여정 완료 및 정리")
    void testCompleteUserJourney() {
        // 12.1 저장된 콘텐츠 목록 확인
        Response savedContentsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_API_PATH + "/saved-contents");
            
        assertThat("저장된 콘텐츠 응답 코드", savedContentsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("저장된 콘텐츠 응답 결과", savedContentsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 12.2 저장된 인용구 목록 확인
        Response savedQuotesResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_API_PATH + "/saved-quotes");
            
        assertThat("저장된 인용구 응답 코드", savedQuotesResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("저장된 인용구 응답 결과", savedQuotesResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 12.3 작성한 게시물 목록 확인
        Response userPostsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_API_PATH + "/posts");
            
        assertThat("작성한 게시물 응답 코드", userPostsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("작성한 게시물 응답 결과", userPostsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 12.4 생성한 콘텐츠 목록 확인
        Response createdContentsResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get(USER_API_PATH + "/created-contents");
            
        assertThat("생성한 콘텐츠 응답 코드", createdContentsResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("생성한 콘텐츠 응답 결과", createdContentsResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
        
        // 12.5 로그아웃
        Response logoutResponse = authHelper.logout();
        
        assertThat("로그아웃 응답 코드", logoutResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("로그아웃 응답 결과", logoutResponse.path("result").toString(), 
                equalToIgnoringCase("success"));
    }
}
