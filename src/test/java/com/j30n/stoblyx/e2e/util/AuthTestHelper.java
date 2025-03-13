package com.j30n.stoblyx.e2e.util;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * E2E 테스트에서 인증 관련 기능을 도와주는 헬퍼 클래스
 */
public class AuthTestHelper {

    private static final String AUTH_API_PATH = "/auth";
    private static final String LOGIN_PATH = AUTH_API_PATH + "/login";
    private static final String SIGNUP_PATH = AUTH_API_PATH + "/signup";
    private static final String LOGOUT_PATH = AUTH_API_PATH + "/logout";
    private static final String REFRESH_PATH = AUTH_API_PATH + "/refresh";
    
    private final RequestSpecification requestSpec;
    private String accessToken;
    private String refreshToken;
    
    public AuthTestHelper(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }
    
    /**
     * 사용자 등록하기
     * 
     * @param username 사용자 이름
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return Response 응답 객체
     */
    public Response signUp(String username, String email, String password, String nickname) {
        Map<String, String> signUpRequest = new HashMap<>();
        signUpRequest.put("username", username);
        signUpRequest.put("email", email);
        signUpRequest.put("password", password);
        signUpRequest.put("nickname", nickname);
        
        return requestSpec
            .contentType(ContentType.JSON)
            .body(signUpRequest)
            .when()
            .post(SIGNUP_PATH);
    }
    
    /**
     * 로그인하기
     * 
     * @param username 사용자 이름
     * @param password 비밀번호
     * @return Response 응답 객체
     */
    public Response login(String username, String password) {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);
        
        Response response = requestSpec
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post(LOGIN_PATH);
            
        if (response.statusCode() == HttpStatus.OK.value()) {
            // 다양한 응답 구조에 대응하기 위해 여러 경로에서 토큰 추출 시도
            try {
                // 기본 경로 시도
                this.accessToken = response.path("data.accessToken");
                this.refreshToken = response.path("data.refreshToken");
                
                // 토큰이 null인 경우 다른 경로 시도
                if (this.accessToken == null) {
                    // 응답 본문 로깅
                    System.out.println("로그인 응답 구조 확인: " + response.asString());
                    
                    // 다른 가능한 경로들 시도
                    this.accessToken = response.path("data.token");
                    if (this.accessToken == null) this.accessToken = response.path("token");
                    if (this.accessToken == null) this.accessToken = response.path("access_token");
                    if (this.accessToken == null) this.accessToken = response.path("data.access_token");
                    
                    this.refreshToken = response.path("data.refresh_token");
                    if (this.refreshToken == null) this.refreshToken = response.path("refresh_token");
                }
                
                // 토큰 추출 결과 로깅
                System.out.println("추출된 액세스 토큰: " + (this.accessToken != null ? "성공" : "실패"));
                System.out.println("추출된 리프레시 토큰: " + (this.refreshToken != null ? "성공" : "실패"));
            } catch (Exception e) {
                System.err.println("토큰 추출 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return response;
    }
    
    /**
     * 로그아웃하기
     * 
     * @return Response 응답 객체
     */
    public Response logout() {
        if (accessToken == null) {
            System.out.println("액세스 토큰이 없습니다. 로그아웃을 건너뜁니다.");
            // 더미 응답 반환
            return requestSpec.when().get("/non-existent-endpoint").then().extract().response();
        }
        
        Response response = requestSpec
            .header(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .when()
            .post(LOGOUT_PATH);
            
        if (response.statusCode() == HttpStatus.OK.value()) {
            this.accessToken = null;
            this.refreshToken = null;
        }
        
        return response;
    }
    
    /**
     * 토큰 갱신하기
     * 
     * @return Response 응답 객체
     */
    public Response refreshToken() {
        if (refreshToken == null) {
            System.out.println("리프레시 토큰이 없습니다. 토큰 갱신을 건너뜁니다.");
            // 더미 응답 반환
            return requestSpec.when().get("/non-existent-endpoint").then().extract().response();
        }
        
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);
        
        Response response = requestSpec
            .contentType(ContentType.JSON)
            .body(refreshRequest)
            .when()
            .post(REFRESH_PATH);
            
        if (response.statusCode() == HttpStatus.OK.value()) {
            this.accessToken = response.path("data.accessToken");
            this.refreshToken = response.path("data.refreshToken");
        }
        
        return response;
    }
    
    /**
     * 인증 헤더 가져오기
     * 
     * @return Headers 인증 헤더
     */
    public Headers getAuthHeaders() {
        if (accessToken == null) {
            System.out.println("액세스 토큰이 없습니다. 빈 헤더를 반환합니다.");
            return new Headers(); // 빈 헤더 반환
        }
        
        return new Headers(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
    }
    
    /**
     * 액세스 토큰 가져오기
     * 
     * @return String 액세스 토큰
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * 리프레시 토큰 가져오기
     * 
     * @return String 리프레시 토큰
     */
    public String getRefreshToken() {
        return refreshToken;
    }
} 