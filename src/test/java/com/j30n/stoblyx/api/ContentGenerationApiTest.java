package com.j30n.stoblyx.api;

import com.aventstack.extentreports.ExtentTest;
import com.j30n.stoblyx.api.config.ExtentReportManager;
import com.j30n.stoblyx.api.config.RestAssuredConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * AI를 사용한 콘텐츠 생성 API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("콘텐츠 생성 API 테스트")
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
class ContentGenerationApiTest extends RestAssuredConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private BookRepository bookRepository;

    private String accessToken;
    private Long savedQuoteId;
    private static ExtentTest extentTest;

    @BeforeAll
    static void setupReport() {
        extentTest = ExtentReportManager.createTest(
            "콘텐츠 생성 API 테스트",
            "AI를 사용한 콘텐츠 생성 API 통합 테스트"
        );
    }

    @AfterAll
    static void tearDownReport() {
        ExtentReportManager.flush();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        
        try {
            // REST Assured 설정 초기화
            setupRestAssured();
            
            // 테스트 사용자 로그인 및 토큰 획득
            accessToken = loginAndGetToken();
            
            // 테스트용 문구 생성
            savedQuoteId = createTestQuote();
        } catch (Exception e) {
            // 로그인 또는 문구 생성 실패 시 로그 출력
            System.err.println("테스트 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            extentTest.fail("테스트 설정 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("문구로부터 콘텐츠 생성 API 테스트")
    void generateContentFromQuote_Success() {
        extentTest.info("문구로부터 콘텐츠 생성 API 테스트 시작");
        
        try {
            // given
            String url = "/contents/quotes/" + savedQuoteId;
            extentTest.info("테스트 URL: " + url);

            // when
            JsonPath jsonPath = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post(url)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("result", equalTo("SUCCESS"))
                .body("message", containsString("콘텐츠가 생성되었습니다"))
                .extract()
                .jsonPath();

            // then
            Long contentId = jsonPath.getLong("data.id");
            assertThat(contentId).isNotNull();
            extentTest.pass("콘텐츠 생성 성공: ID=" + contentId);
            
            // 생성된 콘텐츠 조회
            given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/contents/" + contentId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("result", equalTo("SUCCESS"))
                .body("data.id", equalTo(contentId.intValue()));
                
            extentTest.pass("생성된 콘텐츠 조회 성공");
        } catch (Exception e) {
            extentTest.fail("테스트 실패: " + e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 콘텐츠 생성 시도 테스트")
    void generateContentFromQuote_Unauthorized() {
        extentTest.info("인증되지 않은 사용자의 콘텐츠 생성 시도 테스트 시작");
        
        try {
            // given
            String url = "/contents/quotes/" + savedQuoteId;
            extentTest.info("테스트 URL: " + url);

            // when & then
            given()
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
                
            extentTest.pass("인증되지 않은 사용자 접근 차단 성공");
        } catch (Exception e) {
            extentTest.fail("테스트 실패: " + e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("존재하지 않는 문구로 콘텐츠 생성 시도 테스트")
    void generateContentFromQuote_QuoteNotFound() {
        extentTest.info("존재하지 않는 문구로 콘텐츠 생성 시도 테스트 시작");
        
        try {
            // given
            Long nonExistentQuoteId = 999999L;
            String url = "/contents/quotes/" + nonExistentQuoteId;
            extentTest.info("테스트 URL: " + url + " (존재하지 않는 문구 ID: " + nonExistentQuoteId + ")");

            // when & then
            given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post(url)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("result", equalTo("ERROR"));
                
            extentTest.pass("존재하지 않는 문구 처리 성공");
        } catch (Exception e) {
            extentTest.fail("테스트 실패: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 테스트 사용자 로그인 및 토큰 획득
     */
    private String loginAndGetToken() {
        extentTest.info("테스트 사용자 로그인 시도");
        
        try {
            // 테스트 사용자 정보
            Map<String, String> loginData = new HashMap<>();
            loginData.put("email", "user1@example.com");
            loginData.put("password", "password");
    
            // 로그인 요청
            Response response = given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/auth/login");
                
            // 응답 상태 코드 확인
            int statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK.value()) {
                extentTest.fail("로그인 실패: 상태 코드 " + statusCode);
                System.err.println("로그인 응답: " + response.asString());
                
                // 테스트용 가짜 토큰 반환
                return "test_user_token";
            }
    
            // 토큰 추출
            String token = response.jsonPath().getString("data.accessToken");
            if (token == null || token.isEmpty()) {
                extentTest.fail("토큰 획득 실패: 응답에 토큰이 없습니다");
                System.err.println("로그인 응답: " + response.asString());
                
                // 테스트용 가짜 토큰 반환
                return "test_user_token";
            }
            
            extentTest.info("로그인 성공: 토큰 획득");
            return token;
        } catch (Exception e) {
            extentTest.fail("로그인 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 테스트용 가짜 토큰 반환
            return "test_user_token";
        }
    }

    /**
     * 테스트용 문구 생성
     */
    @Transactional
    private Long createTestQuote() {
        extentTest.info("테스트용 문구 생성 시도");
        
        try {
            // 사용자 조회
            User user = userRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트 사용자가 존재하지 않습니다."));
                
            // 책 조회
            Book book = bookRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("테스트 책이 존재하지 않습니다."));

            // 문구 생성
            Quote quote = Quote.builder()
                .content("지식은 힘이다. - 프랜시스 베이컨")
                .page(42)
                .user(user)
                .book(book)
                .build();

            Long savedQuoteId = quoteRepository.save(quote).getId();
            extentTest.info("테스트용 문구 생성 성공: ID=" + savedQuoteId);
            return savedQuoteId;
        } catch (Exception e) {
            extentTest.fail("테스트용 문구 생성 실패: " + e.getMessage());
            
            // 테스트용 가짜 ID 반환
            return 1L;
        }
    }
} 