package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.application.port.in.user.UserUseCase;
import com.j30n.stoblyx.common.exception.GlobalExceptionHandler;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import com.j30n.stoblyx.support.docs.RestDocsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
@DisplayName("사용자 컨트롤러 테스트")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, RestDocsConfig.class, MonitoringTestConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserInterestUseCase userInterestUseCase;

    // 테스트에 사용할 UserPrincipal 객체
    private UserPrincipal testUserPrincipal;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 UserPrincipal 객체 생성
        testUserPrincipal = UserPrincipal.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .role("USER")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .build();

        // SecurityContext에 인증 정보 설정
        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUserPrincipal,
            null,
            testUserPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("현재 사용자 프로필 조회 API가 정상적으로 동작해야 한다")
    void getCurrentUser() throws Exception {
        // given
        Long userId = 1L;
        UserProfileResponse response = new UserProfileResponse(
            userId,
            "testuser",
            "테스트유저",
            "test@example.com",
            "USER",
            null
        );

        when(userUseCase.getCurrentUser(any())).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    testUserPrincipal,
                    null,
                    testUserPrincipal.getAuthorities()
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
            .andDo(document("user/get-current-user",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 아이디"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                    fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할"),
                    fieldWithPath("data.profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지 URL")
                )
            ));

        verify(userUseCase).getCurrentUser(any());
    }

    @Test
    @DisplayName("사용자 프로필 조회 시 사용자가 인증되지 않은 경우 에러를 반환해야 한다")
    void getCurrentUserWithoutAuth() throws Exception {
        // given
        when(userUseCase.getCurrentUser(any())).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/get-current-user-error",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userUseCase).getCurrentUser(any());
    }

    @Test
    @DisplayName("사용자 프로필 업데이트 API가 정상적으로 동작해야 한다")
    void updateUser() throws Exception {
        // given
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("업데이트된닉네임", "update@example.com", null);
        UserProfileResponse response = new UserProfileResponse(
            userId,
            "testuser",
            "업데이트된닉네임",
            "update@example.com",
            "USER",
            null
        );

        when(userUseCase.updateUser(any(), any(UserUpdateRequest.class))).thenReturn(response);

        // when & then
        try {
            mockMvc.perform(RestDocumentationRequestBuilders.put("/users/me")
                    .with(csrf())
                    .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value("업데이트된닉네임"))
                .andDo(result -> {
                    System.out.println("==== 실제 응답 ====");
                    System.out.println(result.getResponse().getContentAsString());
                    System.out.println("==================");
                })
                .andDo(document("user/update-user",
                        requestFields(
                            fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                            fieldWithPath("email").type(JsonFieldType.STRING).description("변경할 이메일"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("변경할 비밀번호").optional(),
                            fieldWithPath("profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지 URL").optional()
                        ),
                        relaxedResponseFields(
                            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                            fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                            fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 아이디"),
                            fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("변경된 사용자 닉네임"),
                            fieldWithPath("data.email").type(JsonFieldType.STRING).description("변경된 사용자 이메일"),
                            fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할"),
                            fieldWithPath("data.profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지 URL")
                        )
                    ));
        } catch (Exception e) {
            System.out.println("==== 테스트 실패 예외 ====");
            e.printStackTrace();
            System.out.println("========================");
            throw e;
        }

        verify(userUseCase).updateUser(any(), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 프로필 업데이트 시 예외가 발생하면 에러를 반환해야 한다")
    void updateUserWithException() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("업데이트된닉네임", "update@example.com", null);

        when(userUseCase.updateUser(any(), any(UserUpdateRequest.class)))
            .thenThrow(new IllegalArgumentException("유효하지 않은 닉네임입니다."));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/me")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/update-user-error",
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("변경할 이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("변경할 비밀번호").optional(),
                    fieldWithPath("profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지 URL").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userUseCase).updateUser(any(), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 계정 삭제 API가 정상적으로 동작해야 한다")
    void deleteUser() throws Exception {
        // given
        doNothing().when(userUseCase).deleteUser(any());

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/me")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(document("user/delete-user",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userUseCase).deleteUser(any());
    }

    @Test
    @DisplayName("사용자 계정 삭제 시 예외가 발생하면 에러를 반환해야 한다")
    void deleteUserWithException() throws Exception {
        // given
        doThrow(new IllegalArgumentException("계정 삭제 권한이 없습니다."))
            .when(userUseCase).deleteUser(any());

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/me")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/delete-user-error",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userUseCase).deleteUser(any());
    }

    @Test
    @DisplayName("사용자 관심사 조회 API가 정상적으로 동작해야 한다")
    void getUserInterest() throws Exception {
        // given
        Long userId = 1L;
        UserInterestResponse response = UserInterestResponse.builder()
            .userId(userId)
            .genres(List.of("소설", "자기계발"))
            .authors(List.of("김작가", "이작가"))
            .keywords(List.of("독서", "지식"))
            .bio("안녕하세요, 독서를 좋아합니다.")
            .build();

        when(userInterestUseCase.getUserInterest(any())).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me/interests")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.genres[0]").value("소설"))
            .andDo(document("user/get-user-interest",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.genres").type(JsonFieldType.ARRAY).description("관심 장르 목록"),
                    fieldWithPath("data.authors").type(JsonFieldType.ARRAY).description("관심 작가 목록"),
                    fieldWithPath("data.keywords").type(JsonFieldType.ARRAY).description("관심 키워드 목록"),
                    fieldWithPath("data.bio").type(JsonFieldType.STRING).description("자기소개")
                )
            ));

        verify(userInterestUseCase).getUserInterest(any());
    }

    @Test
    @DisplayName("사용자 관심사 조회 시 예외가 발생하면 에러를 반환해야 한다")
    void getUserInterestWithException() throws Exception {
        // given
        when(userInterestUseCase.getUserInterest(any()))
            .thenThrow(new IllegalArgumentException("관심사 정보가 없습니다."));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me/interests")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/get-user-interest-error",
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userInterestUseCase).getUserInterest(any());
    }

    @Test
    @DisplayName("사용자 관심사 업데이트 API가 정상적으로 동작해야 한다")
    void updateUserInterest() throws Exception {
        // given
        Long userId = 1L;
        UserInterestRequest request = new UserInterestRequest(
            List.of("판타지", "SF"),
            List.of("박작가", "최작가"),
            List.of("상상력", "미래"),
            "SF와 판타지 소설을 좋아합니다."
        );

        UserInterestResponse response = UserInterestResponse.builder()
            .userId(userId)
            .genres(List.of("판타지", "SF"))
            .authors(List.of("박작가", "최작가"))
            .keywords(List.of("상상력", "미래"))
            .bio("SF와 판타지 소설을 좋아합니다.")
            .build();

        when(userInterestUseCase.updateUserInterest(any(), any(UserInterestRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/me/interests")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.genres[0]").value("판타지"))
            .andDo(document("user/update-user-interest",
                    requestFields(
                        fieldWithPath("genres").description("관심 장르 목록"),
                        fieldWithPath("authors").description("관심 작가 목록"),
                        fieldWithPath("keywords").description("관심 키워드 목록"),
                        fieldWithPath("bio").description("자기소개")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("data.genres").type(JsonFieldType.ARRAY).description("변경된 관심 장르 목록"),
                        fieldWithPath("data.authors").type(JsonFieldType.ARRAY).description("변경된 관심 작가 목록"),
                        fieldWithPath("data.keywords").type(JsonFieldType.ARRAY).description("변경된 관심 키워드 목록"),
                        fieldWithPath("data.bio").type(JsonFieldType.STRING).description("변경된 자기소개")
                    )
                )
            );

        verify(userInterestUseCase).updateUserInterest(any(), any(UserInterestRequest.class));
    }

    @Test
    @DisplayName("사용자 관심사 업데이트 시 예외가 발생하면 에러를 반환해야 한다")
    void updateUserInterestWithException() throws Exception {
        // given
        UserInterestRequest request = new UserInterestRequest(
            List.of("판타지", "SF"),
            List.of("박작가", "최작가"),
            List.of("상상력", "미래"),
            "SF와 판타지 소설을 좋아합니다."
        );

        when(userInterestUseCase.updateUserInterest(any(), any(UserInterestRequest.class)))
            .thenThrow(new IllegalArgumentException("유효하지 않은 관심사입니다."));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/me/interests")
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/update-user-interest-error",
                    requestFields(
                        fieldWithPath("genres").description("관심 장르 목록"),
                        fieldWithPath("authors").description("관심 작가 목록"),
                        fieldWithPath("keywords").description("관심 키워드 목록"),
                        fieldWithPath("bio").description("자기소개")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                    )
                )
            );

        verify(userInterestUseCase).updateUserInterest(any(), any(UserInterestRequest.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 API가 정상적으로 동작해야 한다")
    void uploadProfileImage() throws Exception {
        // given
        Long userId = 1L;
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        UserProfileResponse response = new UserProfileResponse(
            userId,
            "testuser",
            "테스트유저",
            "test@example.com",
            "USER",
            "https://example.com/images/profile.jpg"
        );

        when(userUseCase.updateProfileImage(any(), any(MultipartFile.class))).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/users/me/profile-image")
                .file(image)
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(document("user/upload-profile-image",
                    requestParts(
                        partWithName("image").description("프로필 이미지 파일")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 아이디"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할"),
                        fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                    )
                )
            );

        verify(userUseCase).updateProfileImage(any(), any(MultipartFile.class));
    }

    @Test
    @DisplayName("프로필 이미지 업로드 시 이미지가 없으면 에러를 반환해야 한다")
    void uploadProfileImageWithoutImage() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "",
            MediaType.IMAGE_JPEG_VALUE,
            new byte[0]
        );

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/users/me/profile-image")
                .file(image)
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("user/upload-profile-image-error",
                requestParts(
                    partWithName("image").description("프로필 이미지 파일")
                ),
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                )
            ));

        verify(userUseCase, never()).updateProfileImage(any(), any(MultipartFile.class));
    }
}