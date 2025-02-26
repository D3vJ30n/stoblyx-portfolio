package com.j30n.stoblyx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.controller.AuthController;
import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;
import com.j30n.stoblyx.adapter.in.web.support.TokenExtractor;
import com.j30n.stoblyx.application.service.auth.AuthService;
import com.j30n.stoblyx.config.RestDocsConfig;
import com.j30n.stoblyx.config.TestControllerAdvice;
import com.j30n.stoblyx.config.TestValidatorConfig;
import com.j30n.stoblyx.config.TestWebSecurityConfig;
import com.j30n.stoblyx.infrastructure.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = {AuthController.class, TestControllerAdvice.class})
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
@Import({TestValidatorConfig.class, TestWebSecurityConfig.class, RestDocsConfig.class})
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenExtractor tokenExtractor;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("회원가입 API - 정상 케이스")
    void signUpSuccess() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
            "testuser",           // username
            "password123!",       // password
            "테스트유저",         // nickname
            "test@example.com"    // email
        );

        doNothing().when(authService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
            .andDo(document("auth-signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").description("사용자 아이디 (3자 이상 50자 이하)"),
                    fieldWithPath("password").description("비밀번호 (8자 이상 100자 이하)"),
                    fieldWithPath("nickname").description("닉네임 (2자 이상 50자 이하)"),
                    fieldWithPath("email").description("이메일 (최대 100자)")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과 (SUCCESS/ERROR)"),
                    fieldWithPath("message").description("처리 결과 메시지"),
                    fieldWithPath("data").description("응답 데이터 (회원가입의 경우 null)")
                )
            ));
    }

    @Test
    @DisplayName("회원가입 API - 이메일 형식 오류")
    void signUp_InvalidEmail() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
            "testuser",           // username
            "password123!",       // password (유효한 비밀번호로 변경)
            "테스트유저",         // nickname
            "invalid-email"       // email (잘못된 형식)
        );

        log.debug("테스트 요청 데이터: {}", objectMapper.writeValueAsString(request));

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다"))
            .andDo(document("auth-signup-invalid-email",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").description("사용자 아이디"),
                    fieldWithPath("password").description("비밀번호"),
                    fieldWithPath("nickname").description("닉네임"),
                    fieldWithPath("email").description("잘못된 형식의 이메일")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과 (ERROR)"),
                    fieldWithPath("message").description("유효성 검사 오류 메시지"),
                    fieldWithPath("data").description("오류 시 null")
                )
            ));
    }

    @Test
    @DisplayName("회원가입 API - 필수 필드 누락")
    void signUp_MissingRequiredField() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
            "tester6",           // username
            "Password123!",      // password
            "테스트유저",        // nickname
            ""                   // email (필수 필드 누락)
        );

        log.debug("테스트 요청 데이터: {}", objectMapper.writeValueAsString(request));

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("이메일은 필수입니다"))
            .andDo(document("auth-signup-missing-field",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").description("사용자 아이디"),
                    fieldWithPath("email").description("누락된 이메일 필드"),
                    fieldWithPath("password").description("비밀번호"),
                    fieldWithPath("nickname").description("닉네임")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과 (ERROR)"),
                    fieldWithPath("message").description("필수 필드 누락 오류 메시지"),
                    fieldWithPath("data").description("오류 시 null")
                )
            ));
    }

    @Test
    @DisplayName("로그인 API - 정상 케이스")
    void loginSuccess() throws Exception {
        // given
        LoginRequest request = new LoginRequest(
            "testuser",
            "password123!"
        );

        given(authService.login(any(LoginRequest.class)))
            .willReturn(TokenResponse.of("test.access.token", "test.refresh.token", 3600L));

        // when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.refreshToken").exists())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.expiresIn").value(3600))
            .andDo(document("auth-login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").description("사용자 아이디"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("처리 결과 메시지"),
                    fieldWithPath("data.accessToken").description("액세스 토큰"),
                    fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                    fieldWithPath("data.tokenType").description("토큰 타입 (Bearer)"),
                    fieldWithPath("data.expiresIn").description("토큰 만료 시간 (초)")
                )
            ));
    }

    @Test
    @DisplayName("회원가입 API - 비밀번호 유효성 검사")
    void signUp_InvalidPassword() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
            "testuser",
            "weak",  // 약한 비밀번호
            "테스트유저",
            "test@example.com"
        );

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다"))
            .andDo(document("auth-signup-invalid-password",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @DisplayName("회원가입 API - 중복 회원가입")
    void signUp_DuplicateUser() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
            "testuser",
            "password123!",
            "테스트유저",
            "test@example.com"
        );

        doThrow(new IllegalArgumentException("이미 존재하는 사용자입니다"))
            .when(authService).signUp(any(SignUpRequest.class));

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("이미 존재하는 사용자입니다"))
            .andDo(document("auth-signup-duplicate",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }
}
