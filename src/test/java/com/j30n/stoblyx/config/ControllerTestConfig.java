package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 테스트 환경에서 컨트롤러 충돌을 방지하기 위한 설정 클래스
 */
@TestConfiguration
@ComponentScan(
    basePackages = "com.j30n.stoblyx.adapter.in.web.controller",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.j30n.stoblyx.adapter.in.web.controller.AdminDashboardController.class,
                com.j30n.stoblyx.adapter.in.web.controller.AdminBookController.class
            }
        )
    }
)
public class ControllerTestConfig {
    // 컨트롤러 충돌을 방지하기 위한 빈 설정 클래스
} 