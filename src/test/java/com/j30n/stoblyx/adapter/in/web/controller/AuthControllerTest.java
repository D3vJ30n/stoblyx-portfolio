package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;
import com.j30n.stoblyx.adapter.in.web.support.TokenExtractor;
import com.j30n.stoblyx.application.service.auth.AuthService;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@DisplayName("인증 컨트롤러 테스트")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class})
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenExtractor tokenExtractor;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(Preprocessors.prettyPrint())
                .withResponseDefaults(Preprocessors.prettyPrint()))
            .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testuser", "Password123!", "Test User", "test@example.com");

        doNothing().when(authService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
            .andDo(document("auth/signup",
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                )
            ));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123");
        TokenResponse response = new TokenResponse("access-token", "refresh-token", 3600);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
            .andDo(document("auth/login",
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.",
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                        fieldWithPath("expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간(초)")
                    )
            ));
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refresh_Success() throws Exception {
        // given
        String refreshToken = "refresh-token";
        TokenResponse response = new TokenResponse("new-access-token", "new-refresh-token", 3600);

        when(tokenExtractor.extractToken(any())).thenReturn(refreshToken);
        when(authService.refreshToken(refreshToken)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
            .andDo(document("auth/refresh",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 리프레시 토큰")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.",
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로운 액세스 토큰"),
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("새로운 리프레시 토큰"),
                        fieldWithPath("expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간(초)")
                    )
            ));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() throws Exception {
        // given
        String accessToken = "access-token";

        when(tokenExtractor.extractToken(any())).thenReturn(accessToken);
        doNothing().when(authService).logout(accessToken);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(document("auth/logout",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)")
                )
            ));
    }
} 