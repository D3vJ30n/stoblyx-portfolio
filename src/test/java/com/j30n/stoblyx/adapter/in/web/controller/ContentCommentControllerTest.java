package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentUpdateRequest;
import com.j30n.stoblyx.application.port.in.content.ContentCommentUseCase;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.support.docs.RestDocsConfig;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentCommentController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({RestDocsConfig.class, SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("ContentCommentController 테스트")
class ContentCommentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private RequestPostProcessor testUser;

    @MockBean
    private ContentCommentUseCase contentCommentUseCase;

    private ContentCommentResponse mockCommentResponse;

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

        this.testUser = request -> {
            request.setAttribute("userId", 1L);
            return RestDocsUtils.getTestUser().postProcessRequest(request);
        };

        // ContentCommentResponse.UserInfo 객체 생성
        ContentCommentResponse.UserInfo userInfo = new ContentCommentResponse.UserInfo(
            1L, "testuser", "테스트유저", "http://example.com/profile.jpg");

        // 테스트용 댓글 응답 객체 생성
        mockCommentResponse = new ContentCommentResponse(
            1L, "테스트 댓글입니다.", userInfo, 1L, null, 0,
            Collections.emptyList(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment() throws Exception {
        // Given
        Long contentId = 1L;
        Long userId = 1L;
        ContentCommentCreateRequest request = new ContentCommentCreateRequest("테스트 댓글입니다.", null);

        given(contentCommentUseCase.createComment(eq(contentId), any(ContentCommentCreateRequest.class), eq(userId)))
            .willReturn(mockCommentResponse);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/contents/{contentId}/comments", contentId)
                .with(testUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(mockCommentResponse.id()))
            .andExpect(jsonPath("$.data.commentText").value(mockCommentResponse.commentText()))
            .andDo(document("content-comment/create-comment",
                pathParameters(
                    parameterWithName("contentId").description("콘텐츠 ID")
                ),
                requestFields(
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID (대댓글인 경우에만 값이 존재)")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData()
                ).andWithPrefix("data.",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("user.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                    fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID"),
                    fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));

        verify(contentCommentUseCase).createComment(eq(contentId), any(ContentCommentCreateRequest.class), eq(userId));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment() throws Exception {
        // Given
        Long commentId = 1L;
        Long userId = 1L;
        ContentCommentUpdateRequest request = new ContentCommentUpdateRequest("수정된 댓글입니다.");

        given(contentCommentUseCase.updateComment(eq(commentId), any(ContentCommentUpdateRequest.class), eq(userId)))
            .willReturn(mockCommentResponse);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/contents/comments/{commentId}", commentId)
                .with(testUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(mockCommentResponse.id()))
            .andDo(document("content-comment/update-comment",
                pathParameters(
                    parameterWithName("commentId").description("수정할 댓글 ID")
                ),
                requestFields(
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData()
                ).andWithPrefix("data.",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("user.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                    fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID"),
                    fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));

        verify(contentCommentUseCase).updateComment(eq(commentId), any(ContentCommentUpdateRequest.class), eq(userId));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() throws Exception {
        // Given
        Long commentId = 1L;
        Long userId = 1L;

        doNothing().when(contentCommentUseCase).deleteComment(commentId, userId);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/contents/comments/{commentId}", commentId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(document("content-comment/delete-comment",
                pathParameters(
                    parameterWithName("commentId").description("삭제할 댓글 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)")
                )
            ));

        verify(contentCommentUseCase).deleteComment(commentId, userId);
    }

    @Test
    @DisplayName("최상위 댓글 조회 테스트")
    void getTopLevelComments() throws Exception {
        // Given
        Long contentId = 1L;
        List<ContentCommentResponse> comments = Collections.singletonList(mockCommentResponse);
        PageImpl<ContentCommentResponse> commentPage = new PageImpl<>(comments);

        given(contentCommentUseCase.getTopLevelCommentsByContent(eq(contentId), any(Pageable.class)))
            .willReturn(commentPage);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/{contentId}/comments", contentId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].id").value(mockCommentResponse.id()))
            .andExpect(jsonPath("$.data.content[0].commentText").value(mockCommentResponse.commentText()))
            .andDo(document("content-comment/get-top-level-comments",
                pathParameters(
                    parameterWithName("contentId").description("콘텐츠 ID")
                ),
                queryParameters(
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 방식 (예: createdAt,desc)").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("댓글 목록")
                ).andWithPrefix("data.content[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("user.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                    fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID"),
                    fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));

        verify(contentCommentUseCase).getTopLevelCommentsByContent(eq(contentId), any(Pageable.class));
    }

    @Test
    @DisplayName("답글 조회 테스트")
    void getReplies() throws Exception {
        // Given
        Long commentId = 1L;
        List<ContentCommentResponse> replies = Collections.singletonList(mockCommentResponse);

        given(contentCommentUseCase.getRepliesByParentId(eq(commentId)))
            .willReturn(replies);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/comments/{commentId}/replies", commentId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].id").value(mockCommentResponse.id()))
            .andExpect(jsonPath("$.data[0].commentText").value(mockCommentResponse.commentText()))
            .andDo(document("content-comment/get-replies",
                pathParameters(
                    parameterWithName("commentId").description("부모 댓글 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터")
                ).andWithPrefix("data[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("user.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                    fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID"),
                    fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));

        verify(contentCommentUseCase).getRepliesByParentId(eq(commentId));
    }

    @Test
    @DisplayName("사용자 댓글 조회 테스트")
    void getUserComments() throws Exception {
        // Given
        Long userId = 1L;
        List<ContentCommentResponse> comments = Collections.singletonList(mockCommentResponse);
        PageImpl<ContentCommentResponse> commentPage = new PageImpl<>(comments);

        given(contentCommentUseCase.getCommentsByUser(eq(userId), any(Pageable.class)))
            .willReturn(commentPage);

        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/users/{userId}/comments", userId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].id").value(mockCommentResponse.id()))
            .andExpect(jsonPath("$.data.content[0].commentText").value(mockCommentResponse.commentText()))
            .andDo(document("content-comment/get-user-comments",
                pathParameters(
                    parameterWithName("userId").description("사용자 ID")
                ),
                queryParameters(
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 방식 (예: createdAt,desc)").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("댓글 목록")
                ).andWithPrefix("data.content[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("commentText").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                    fieldWithPath("user.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                    fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                    fieldWithPath("parentId").type(JsonFieldType.NULL).description("부모 댓글 ID"),
                    fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("replies").type(JsonFieldType.ARRAY).description("답글 목록"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                    fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 일시")
                )
            ));

        verify(contentCommentUseCase).getCommentsByUser(eq(userId), any(Pageable.class));
    }
} 