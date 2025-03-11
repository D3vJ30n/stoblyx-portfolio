package com.j30n.stoblyx.e2e;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import com.j30n.stoblyx.StoblyxApplication;
import com.j30n.stoblyx.config.SecurityTestConfig;

/**
 * E2E 테스트의 기본 설정을 담당하는 클래스
 * 모든 E2E 테스트는 이 클래스를 상속받아 구현합니다.
 */
@SpringBootTest(
    classes = {StoblyxApplication.class, TestApplication.class, SecurityTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseE2ETest {

    @LocalServerPort
    private int port;
    
    protected RequestSpecification requestSpec;
    
    /**
     * 모든 테스트 실행 전 기본 설정
     */
    @BeforeAll
    public void setUpAll() {
        // E2E 테스트를 위한 전역 설정
    }
    
    /**
     * 각 테스트 실행 전 기본 설정
     */
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
        
        // 기본 요청 명세 설정
        requestSpec = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();
    }
    
    /**
     * 기본 요청 명세를 반환하는 메서드
     * @return RequestSpecification 기본 요청 명세
     */
    protected RequestSpecification createRequestSpec() {
        return RestAssured.given().spec(requestSpec);
    }
} 