package com.j30n.stoblyx;

import com.j30n.stoblyx.config.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스토블릭스 애플리케이션 통합 테스트
 * 
 * 테스트 환경:
 * - H2 인메모리 데이터베이스 사용
 * - Redis 모킹 처리 (TestRedisConfig)
 * - 보안 설정 모킹 처리 (TestSecurityConfig)
 */
@SpringBootTest(
    classes = {
        StoblyxApplication.class,
        TestSecurityConfig.class,
        TestRedisConfig.class,
        TestDataConfig.class,
        TestKoBartConfig.class,
        TestControllerAdvice.class,
        TestRestTemplateConfig.class
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
        "kobart.api.url=http://localhost:5000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
    }
)
@Transactional
class StoblyxApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("스프링 컨텍스트 로드 테스트")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(redisTemplate).isNotNull();
    }

    @Test
    @DisplayName("필수 빈 로드 테스트")
    void requiredBeansLoaded() {
        // 기본 인프라 빈
        assertThat(applicationContext.containsBean("redisTemplate")).isTrue();
        assertThat(applicationContext.containsBean("stringRedisTemplate")).isTrue();
        assertThat(applicationContext.containsBean("entityManagerFactory")).isTrue();
        assertThat(applicationContext.containsBean("transactionManager")).isTrue();
        assertThat(applicationContext.containsBean("restTemplate")).isTrue();

        // 보안 관련 빈
        assertThat(applicationContext.containsBean("securityFilterChain")).isTrue();
        assertThat(applicationContext.containsBean("passwordEncoder")).isTrue();
        assertThat(applicationContext.containsBean("authenticationManager")).isTrue();
        assertThat(applicationContext.containsBean("userDetailsService")).isTrue();

        // 리포지토리 빈
        assertThat(applicationContext.containsBean("userRepository")).isTrue();
        assertThat(applicationContext.containsBean("bookRepository")).isTrue();
        assertThat(applicationContext.containsBean("quoteRepository")).isTrue();

        // AI 서비스 빈
        assertThat(applicationContext.containsBean("pikaLabsClient")).isTrue();
        assertThat(applicationContext.containsBean("ttsService")).isTrue();
        assertThat(applicationContext.containsBean("bgmService")).isTrue();
    }
}
