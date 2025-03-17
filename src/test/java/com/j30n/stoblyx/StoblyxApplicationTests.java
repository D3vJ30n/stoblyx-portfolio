package com.j30n.stoblyx;

import com.j30n.stoblyx.config.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스토블릭스 애플리케이션 통합 테스트
 * <p>
 * 테스트 환경:
 * - H2 인메모리 데이터베이스 사용
 * - Redis 모킹 처리 (TestRedisConfig)
 * - 보안 설정 모킹 처리 (TestSecurityConfig)
 */
@SpringBootTest(
    classes = {
        StoblyxApplication.class,
        SecurityTestConfig.class,
        RedisTestConfig.class,
        DataTestConfig.class,
        KoBartTestConfig.class,
        ControllerTestAdvice.class,
        RestTemplateTestConfig.class,
        JacksonTestConfig.class,
        RandomTestConfig.class,
        XssTestConfig.class
    }
)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.redis.enabled=true",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "spring.data.redis.password=",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
            "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "kobart.api.url=http://localhost:8000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.main.web-application-type=none",
        "jwt.secret=test_jwt_secret_key_for_testing_purposes_only",
        "jwt.access-token-validity-in-seconds=3600",
        "jwt.refresh-token-validity-in-seconds=86400"
    }
)
@Transactional
class StoblyxApplicationTests {

    @Test
    @DisplayName("스프링 컨텍스트 로드 테스트")
    void contextLoads() {
        // 컨텍스트가 로드되면 테스트 성공
    }
}
