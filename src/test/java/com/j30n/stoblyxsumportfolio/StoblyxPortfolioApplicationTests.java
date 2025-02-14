package com.j30n.stoblyxsumportfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StoblyxPortfolioApplicationTests {

    @Test
    void contextLoads() {
        // 애플리케이션이 정상적으로 로드되는지 확인하는 기본 테스트
        assertThat(true).isTrue();
    }
}
