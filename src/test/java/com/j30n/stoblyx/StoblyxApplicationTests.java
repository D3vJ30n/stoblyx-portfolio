package com.j30n.stoblyx;

import com.j30n.stoblyx.config.TestDataConfig;
import com.j30n.stoblyx.config.TestKoBartConfig;
import com.j30n.stoblyx.config.TestRedisConfig;
import com.j30n.stoblyx.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
        StoblyxApplication.class,
        TestSecurityConfig.class,
        TestRedisConfig.class,
        TestDataConfig.class,
        TestKoBartConfig.class
    }
)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.redis.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
        "kobart.api.url=http://localhost:5000"
    },
    locations = "classpath:application-test.yml"
)
@Transactional
class StoblyxApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("스프링 컨텍스트 로드 테스트")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("필수 빈 로드 테스트")
    void requiredBeansLoaded() {
        // when & then
        assertThat(applicationContext.getBean(RedisTemplate.class)).isNotNull();
        assertThat(applicationContext.getBean(SecurityFilterChain.class)).isNotNull();
        assertThat(applicationContext.getBean(PasswordEncoder.class)).isNotNull();
    }
}
