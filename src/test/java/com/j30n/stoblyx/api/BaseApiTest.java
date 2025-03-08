package com.j30n.stoblyx.api;

import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * API 테스트를 위한 기본 클래스
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3307/stoblyx_db",
        "spring.datasource.username=stoblyx_user",
        "spring.datasource.password=6188",
        "spring.redis.host=localhost",
        "spring.redis.port=6379"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest extends RestAssuredConfig {

    // 테스트용 사용자 및 토큰 정보
    protected static final String TEST_USER_EMAIL = "test@example.com";
    protected static final String TEST_USER_PASSWORD = "test1234";
    protected static final String ADMIN_USER_EMAIL = "admin@example.com";
    protected static final String ADMIN_USER_PASSWORD = "admin1234";
    
    protected String userToken;
    protected String adminToken;

    /**
     * 모든 테스트 전에 실행되는 초기화 메서드
     */
    @BeforeAll
    public void init() {
        // 로그인하여 테스트용 사용자 토큰 획득
        userToken = loginAndGetToken(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        adminToken = loginAndGetToken(ADMIN_USER_EMAIL, ADMIN_USER_PASSWORD);
    }

    /**
     * 로그인하여 JWT 토큰 획득
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return JWT 토큰
     */
    protected String loginAndGetToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        Response response = given(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .extract().response();
        
        if (response.statusCode() != 200) {
            // 로그인 요청이 실패한 경우 기본적인 디버그 정보 출력
            System.err.println("로그인 실패: " + response.statusCode());
            System.err.println("응답 본문: " + response.body().asString());
            
            // 테스트 사용자가 없는 경우 회원가입 진행
            return createUserAndLogin(email, password);
        }
        
        return response.jsonPath().getString("data.token");
    }
    
    /**
     * 테스트 사용자 생성 및 로그인
     * 
     * @param email 이메일
     * @param password 비밀번호
     * @return JWT 토큰
     */
    private String createUserAndLogin(String email, String password) {
        // 회원가입 요청 본문 구성
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("email", email);
        registerRequest.put("password", password);
        registerRequest.put("nickname", email.split("@")[0]);
        
        // 회원가입 요청
        Response registerResponse = given(requestSpec)
                .body(registerRequest)
                .when()
                .post("/auth/register")
                .then()
                .extract().response();
        
        if (registerResponse.statusCode() != 201) {
            System.err.println("회원가입 실패: " + registerResponse.statusCode());
            System.err.println("응답 본문: " + registerResponse.body().asString());
            throw new RuntimeException("테스트 사용자 생성 실패");
        }
        
        // 회원가입 후 로그인 재시도
        Response loginResponse = given(requestSpec)
                .body(new LoginRequest(email, password))
                .when()
                .post("/auth/login")
                .then()
                .extract().response();
        
        if (loginResponse.statusCode() != 200) {
            System.err.println("생성 후 로그인 실패: " + loginResponse.statusCode());
            System.err.println("응답 본문: " + loginResponse.body().asString());
            throw new RuntimeException("테스트 사용자 로그인 실패");
        }
        
        return loginResponse.jsonPath().getString("data.token");
    }
} 