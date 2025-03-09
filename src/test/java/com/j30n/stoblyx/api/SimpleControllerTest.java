package com.j30n.stoblyx.api;

import com.j30n.stoblyx.adapter.in.web.controller.HealthCheckController;
import com.j30n.stoblyx.config.MonitoringInterceptor;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.XssExclusionTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단순화된 컨트롤러 테스트
 * WebMvcTest를 사용하여 HealthCheckController만 테스트합니다.
 */
@WebMvcTest(
    controllers = HealthCheckController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = MonitoringInterceptor.class
    )
)
@Import({MonitoringTestConfig.class, XssExclusionTestConfig.class})
@ActiveProfiles("test")
public class SimpleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthEndpoint healthEndpoint;

    @Test
    @DisplayName("헬스 체크 API 테스트")
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testHealthCheck() throws Exception {
        // HealthEndpoint 모킹
        Health health = Health.up().build();
        when(healthEndpoint.health()).thenReturn(health);
        
        // 테스트 실행
        mockMvc.perform(get("/health")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"));
    }
} 