package com.j30n.stoblyx;

import com.j30n.stoblyx.config.TestRedisConfig;
import com.j30n.stoblyx.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스토블릭스 애플리케이션 통합 테스트
 * 
 * 테스트 환경:
 * - H2 인메모리 데이터베이스 사용
 * - Redis 모킹 처리 (TestRedisConfig)
 * - 보안 설정 모킹 처리 (TestSecurityConfig)
 */
@SpringBootTest(classes = {
    StoblyxApplication.class,
    TestSecurityConfig.class,
    TestRedisConfig.class
})
@ActiveProfiles("test")
class StoblyxApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("스프링 컨텍스트 로드 테스트")
    void contextLoads() {
        // when & then
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("필수 빈 로드 테스트")
    void requiredBeansLoaded() {
        // when & then
        assertThat(applicationContext.containsBean("testRedisTemplate")).isTrue();
        assertThat(applicationContext.containsBean("testSecurityFilterChain")).isTrue();
        assertThat(applicationContext.containsBean("testPasswordEncoder")).isTrue();
    }
}
