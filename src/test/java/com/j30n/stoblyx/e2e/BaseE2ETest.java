package com.j30n.stoblyx.e2e;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import com.j30n.stoblyx.StoblyxApplication;
import com.j30n.stoblyx.config.SecurityTestConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.logging.Logger;

/**
 * E2E 테스트의 기본 설정을 담당하는 클래스
 * 모든 E2E 테스트는 이 클래스를 상속받아 구현합니다.
 */
@SpringBootTest(
    classes = {StoblyxApplication.class, TestApplication.class, SecurityTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.sql.init.mode=always",
        "spring.sql.init.continue-on-error=true",
        "spring.jpa.defer-datasource-initialization=true"
    }
)

@ActiveProfiles("e2e") // e2e 프로필 활성화
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseE2ETest {

    private static final Logger logger = Logger.getLogger(BaseE2ETest.class.getName());

    @LocalServerPort
    private int port;
    
    protected RequestSpecification requestSpec;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    /**
     * 모든 테스트 실행 전 기본 설정
     */
    @BeforeAll
    public void setUpAll() {
        // E2E 테스트를 위한 전역 설정
        logger.info("E2E 테스트 시작: 실제 MySQL 데이터베이스 사용 (운영환경과 동일)");
    }
    
    /**
     * 모든 테스트 실행 후 정리 작업
     */
    @AfterAll
    public void tearDownAll() {
        try {
            cleanDatabase();
            logger.info("E2E 테스트 종료: 데이터베이스 정리 완료");
        } catch (Exception e) {
            logger.severe("데이터베이스 정리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
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
    
    /**
     * 테스트 후 데이터베이스를 정리합니다.
     * 모든 테이블의 데이터를 삭제하여 다음 테스트에 영향을 주지 않도록 합니다.
     */
    protected void cleanDatabase() {
        transactionTemplate.execute(status -> {
            try {
                // 외래 키 제약 조건 비활성화
                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
                
                // 테이블 목록 조회 (MySQL 구문)
                @SuppressWarnings("unchecked")
                List<String> tableNames = entityManager.createNativeQuery(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_SCHEMA = DATABASE() " +
                    "AND TABLE_NAME != 'flyway_schema_history'")
                    .getResultList();
                    
                for (String tableName : tableNames) {
                    logger.info("테이블 정리: " + tableName);
                    try {
                        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                    } catch (Exception e) {
                        logger.warning("테이블 " + tableName + " 정리 중 오류: " + e.getMessage());
                    }
                }
                
                // 외래 키 제약 조건 다시 활성화
                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                logger.info("데이터베이스 정리 완료");
                
                return null;
            } catch (Exception e) {
                logger.severe("데이터베이스 정리 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                status.setRollbackOnly();
                return null;
            }
        });
    }
} 