package com.j30n.stoblyx.api.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

/**
 * REST Assured 테스트를 위한 기본 설정 클래스
 */
public abstract class RestAssuredConfig {

    @LocalServerPort
    protected int port;

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    /**
     * 테스트 시작 시 REST Assured 기본 설정 초기화
     */
    @BeforeEach
    public void initRestAssured() {
        if (requestSpec == null) {
            setupRestAssured();
        }
    }

    /**
     * REST Assured 설정 초기화
     */
    protected void setupRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        // 글로벌 로깅 설정
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        // 커스텀 필터 생성 (ExtentReports 로깅용)
        RestAssuredFilter reportingFilter = new RestAssuredFilter();

        // 기본 요청 스펙 설정
        requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost:" + port)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(reportingFilter)  // ExtentReports 로깅 필터 추가
            .log(LogDetail.ALL)          // 콘솔에도 로깅
            .build();

        // 기본 응답 스펙 설정
        responseSpec = new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .log(LogDetail.ALL)          // 콘솔에도 로깅
            .build();
    }

    /**
     * REST Assured의 기본 요청을 반환
     */
    protected RequestSpecification createRequestSpec() {
        if (requestSpec == null) {
            setupRestAssured();
        }
        // CSRF 토큰 추가
        return RestAssured.given().spec(requestSpec)
            .header("X-CSRF-TOKEN", "test-csrf-token");
    }

    /**
     * JWT 토큰 인증을 위한 요청 스펙 생성
     *
     * @param token JWT 토큰
     * @return 인증 설정이 포함된 요청 스펙
     */
    protected RequestSpecification givenAuth(String token) {
        if (requestSpec == null) {
            setupRestAssured();
        }

        if (token == null || token.isEmpty()) {
            // 토큰이 없는 경우 테스트 토큰 사용
            token = "test_admin_token";
        }

        // 테스트 환경에서는 특별한 토큰 처리 (테스트 토큰은 단순히 "test"로 시작함)
        if (token.startsWith("test_")) {
            // 테스트용 가짜 인증 헤더 추가
            return createRequestSpec()
                .header("Authorization", "Bearer " + token)
                .header("X-TEST-AUTH", "true")
                .header("X-TEST-ROLE", token.contains("admin") ? "ROLE_ADMIN" : "ROLE_USER");
        }

        return createRequestSpec().header("Authorization", "Bearer " + token);
    }

    /**
     * 200 OK 응답을 예상하는 응답 스펙 생성
     *
     * @return 200 OK 응답 스펙
     */
    protected ResponseSpecification expectOk() {
        return responseSpec.statusCode(HttpStatus.OK.value());
    }

    /**
     * 201 Created 응답을 예상하는 응답 스펙 생성
     *
     * @return 201 Created 응답 스펙
     */
    protected ResponseSpecification expectCreated() {
        return responseSpec.statusCode(HttpStatus.CREATED.value());
    }

    /**
     * 204 No Content 응답을 예상하는 응답 스펙 생성
     *
     * @return 204 No Content 응답 스펙
     */
    protected ResponseSpecification expectNoContent() {
        return responseSpec.statusCode(HttpStatus.NO_CONTENT.value());
    }

    /**
     * 400 Bad Request 응답을 예상하는 응답 스펙 생성
     *
     * @return 400 Bad Request 응답 스펙
     */
    protected ResponseSpecification expectBadRequest() {
        return responseSpec.statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 401 Unauthorized 응답을 예상하는 응답 스펙 생성
     *
     * @return 401 Unauthorized 응답 스펙
     */
    protected ResponseSpecification expectUnauthorized() {
        return responseSpec.statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * 403 Forbidden 응답을 예상하는 응답 스펙 생성
     *
     * @return 403 Forbidden 응답 스펙
     */
    protected ResponseSpecification expectForbidden() {
        return responseSpec.statusCode(HttpStatus.FORBIDDEN.value());
    }

    /**
     * 404 Not Found 응답을 예상하는 응답 스펙 생성
     *
     * @return 404 Not Found 응답 스펙
     */
    protected ResponseSpecification expectNotFound() {
        return responseSpec.statusCode(HttpStatus.NOT_FOUND.value());
    }

    /**
     * 409 Conflict 응답을 예상하는 응답 스펙 생성
     *
     * @return 409 Conflict 응답 스펙
     */
    protected ResponseSpecification expectConflict() {
        return responseSpec.statusCode(HttpStatus.CONFLICT.value());
    }
} 