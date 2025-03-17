package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.config.WebMvcTestConfig;
import com.j30n.stoblyx.support.docs.RestDocsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
@DisplayName("헬스 체크 컨트롤러 테스트")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import(WebMvcTestConfig.class)
class HealthCheckControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private HealthEndpoint healthEndpoint;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(Preprocessors.prettyPrint())
                .withResponseDefaults(Preprocessors.prettyPrint()))
            .apply(springSecurity())
            .build();
    }

    @Test
    @DisplayName("기본 헬스 체크 API가 정상적으로 동작해야 한다")
    void healthCheck() throws Exception {
        // given
        Health healthData = Health.up().build();
        when(healthEndpoint.health()).thenReturn(healthData);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.status").value("UP"))
            .andExpect(jsonPath("$.data.timestamp").exists())
            .andDo(document("health/check",
                responseFields(RestDocsUtils.getBasicHealthCheckResponseFields())
            ));
    }

    @Test
    @DisplayName("상세 헬스 체크 API가 정상적으로 동작해야 한다")
    void detailedHealthCheck() throws Exception {
        // given
        Map<String, Object> details = new HashMap<>();
        details.put("db", Map.of("status", "UP", "details", Map.of("database", "H2", "validationQuery", "isValid()")));
        details.put("diskSpace", Map.of("status", "UP", "details", Map.of("total", 1000000, "free", 500000)));

        Health healthData = Health.up()
            .withDetails(details)
            .build();

        when(healthEndpoint.health()).thenReturn(healthData);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/health/details"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.status").value("UP"))
            .andExpect(jsonPath("$.data.timestamp").exists())
            .andExpect(jsonPath("$.data.details").exists())
            .andDo(document("health/details",
                RestDocsUtils.getRelaxedHealthCheckResponseFields()
            ));
    }
} 