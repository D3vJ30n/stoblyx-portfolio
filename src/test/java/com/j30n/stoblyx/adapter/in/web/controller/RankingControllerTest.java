package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.ranking.RankingActivityRequest;
import com.j30n.stoblyx.application.port.in.ranking.RankingUserScoreUseCase;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.support.docs.RestDocsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankingController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("랭킹 컨트롤러 테스트")
class RankingControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestPostProcessor testUser;

    @MockBean
    private RankingUserScoreUseCase rankingUserScoreUseCase;

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

        this.testUser = RestDocsUtils.getTestUser();
    }

    @Test
    @DisplayName("상위 사용자 랭킹 조회 API가 정상적으로 동작해야 한다")
    void getTopUsers() throws Exception {
        // given
        RankingUserScore user1 = createRankingUser(1L, 500, RankType.PLATINUM);
        RankingUserScore user2 = createRankingUser(2L, 400, RankType.GOLD);

        List<RankingUserScore> topUsers = List.of(user1, user2);
        when(rankingUserScoreUseCase.getTopUsers(anyInt())).thenReturn(topUsers);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/ranking/top")
                .param("limit", "10")
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].userId").exists())
            .andExpect(jsonPath("$.data[0].score").exists())
            .andDo(document("ranking/top-users",
                queryParameters(
                    parameterWithName("limit").description("조회할 상위 사용자 수")
                ),
                responseFields(RestDocsUtils.getTopRankingResponseFields())
            ));
    }

    @Test
    @DisplayName("랭크 타입별 사용자 목록 조회 API가 정상적으로 동작해야 한다")
    void getUsersByRankType() throws Exception {
        // given
        RankingUserScore user1 = createRankingUser(1L, 500, RankType.GOLD);
        RankingUserScore user2 = createRankingUser(2L, 400, RankType.GOLD);

        List<RankingUserScore> goldUsers = List.of(user1, user2);
        when(rankingUserScoreUseCase.getUsersByRankType(any(RankType.class))).thenReturn(goldUsers);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/ranking/users")
                .param("rankType", "GOLD")
                .param("page", "0")
                .param("size", "10")
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content[0].userId").exists())
            .andExpect(jsonPath("$.data.content[0].score").exists())
            .andDo(document("ranking/users-by-rank-type",
                queryParameters(
                    parameterWithName("rankType").description("랭크 타입 (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND)"),
                    parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                    parameterWithName("size").description("페이지 크기")
                ),
                responseFields(RestDocsUtils.getRankingPageResponseFields())
            ));
    }

    @Test
    @DisplayName("랭킹 통계 조회 API가 정상적으로 동작해야 한다")
    void getRankingStatistics() throws Exception {
        // given
        Map<String, Long> rankDistribution = new HashMap<>();
        rankDistribution.put("BRONZE", 100L);
        rankDistribution.put("SILVER", 50L);
        rankDistribution.put("GOLD", 20L);
        rankDistribution.put("PLATINUM", 10L);
        rankDistribution.put("DIAMOND", 5L);

        when(rankingUserScoreUseCase.getUsersByRankType(any(RankType.class))).thenReturn(List.of());
        when(rankingUserScoreUseCase.getTopUsers(anyInt())).thenReturn(List.of());

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/ranking/statistics")
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(document("ranking/statistics",
                responseFields(RestDocsUtils.getRankingStatisticsResponseFields())
            ));
    }

    @Test
    @DisplayName("활동 점수 업데이트 API가 정상적으로 동작해야 한다")
    void updateActivityScore() throws Exception {
        // given
        RankingActivityRequest request = new RankingActivityRequest("CONTENT_CREATE", 10);
        RankingUserScore updatedScore = createRankingUser(1L, 510, RankType.PLATINUM);

        when(rankingUserScoreUseCase.updateUserScore(anyLong(), anyInt())).thenReturn(updatedScore);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/ranking/activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(document("ranking/activity",
                requestFields(
                    fieldWithPath("activityType").type(JsonFieldType.STRING).description("활동 유형 (CONTENT_CREATE, COMMENT_CREATE, LIKE, SHARE 등)"),
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("활동 점수")
                ),
                responseFields(RestDocsUtils.getRankingActivityResponseFields())
            ));
    }

    /**
     * 테스트용 RankingUserScore 생성 헬퍼 메서드
     */
    private RankingUserScore createRankingUser(Long userId, Integer score, RankType rankType) {
        RankingUserScore userScore = new RankingUserScore();
        userScore.setUserId(userId);
        userScore.setCurrentScore(score);
        userScore.setRankType(rankType);
        userScore.setLastActivityDate(LocalDateTime.now());
        userScore.setSuspiciousActivity(false);
        userScore.setReportCount(0);
        userScore.setAccountSuspended(false);
        userScore.setIsDeleted(false);
        return userScore;
    }
} 