package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import com.j30n.stoblyx.application.service.comment.CommentService;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@DisplayName("댓글 컨트롤러 테스트")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
class CommentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private CommentService commentService;
    
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
    @DisplayName("댓글 생성 API가 정상적으로 동작해야 한다")
    void createComment() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest("정말 좋은 문구입니다!");
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo(1L, "testuser", "테스트유저", null);
        CommentResponse response = new CommentResponse(
            1L,
            "정말 좋은 문구입니다!",
            userInfo,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(commentService.createComment(eq(1L), any(CommentCreateRequest.class), eq(1L))).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/comments/quotes/{quoteId}", 1L)
                .with(testUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value("정말 좋은 문구입니다!"))
            .andDo(document("comment/create-comment",
                pathParameters(
                    parameterWithName("quoteId").description("인용구 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                .andWithPrefix("data.",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("quoteId").type(JsonFieldType.NUMBER).description("인용구 ID"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("작성자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("작성자 아이디"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("user.profileImage").type(JsonFieldType.NULL).description("프로필 이미지 URL"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
            ));

        verify(commentService).createComment(eq(1L), any(CommentCreateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("댓글 수정 API가 정상적으로 동작해야 한다")
    void updateComment() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용입니다!");
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo(1L, "testuser", "테스트유저", null);
        CommentResponse response = new CommentResponse(
            1L,
            "수정된 댓글 내용입니다!",
            userInfo,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(commentService.updateComment(eq(1L), any(CommentUpdateRequest.class), eq(1L))).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/comments/{commentId}", 1L)
                .with(testUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value("수정된 댓글 내용입니다!"))
            .andDo(document("comment/update-comment",
                pathParameters(
                    parameterWithName("commentId").description("수정할 댓글 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                .andWithPrefix("data.",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정된 댓글 내용"),
                    fieldWithPath("quoteId").type(JsonFieldType.NUMBER).description("인용구 ID"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("작성자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("작성자 아이디"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("user.profileImage").type(JsonFieldType.NULL).description("프로필 이미지 URL"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
            ));

        verify(commentService).updateComment(eq(1L), any(CommentUpdateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("댓글 삭제 API가 정상적으로 동작해야 한다")
    void deleteComment() throws Exception {
        // given
        doNothing().when(commentService).deleteComment(eq(1L), eq(1L));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/comments/{commentId}", 1L)
                .with(testUser))
            .andExpect(status().isNoContent())
            .andDo(document("comment/delete-comment",
                pathParameters(
                    parameterWithName("commentId").description("삭제할 댓글 ID")
                )
            ));

        verify(commentService).deleteComment(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("댓글 조회 API가 정상적으로 동작해야 한다")
    void getComment() throws Exception {
        // given
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo(1L, "testuser", "테스트유저", null);
        CommentResponse response = new CommentResponse(
            1L,
            "정말 좋은 문구입니다!",
            userInfo,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(commentService.getComment(1L)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/comments/{commentId}", 1L)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value("정말 좋은 문구입니다!"))
            .andDo(document("comment/get-comment",
                pathParameters(
                    parameterWithName("commentId").description("조회할 댓글 ID")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                .andWithPrefix("data.",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("quoteId").type(JsonFieldType.NUMBER).description("인용구 ID"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("작성자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("작성자 아이디"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("user.profileImage").type(JsonFieldType.NULL).description("프로필 이미지 URL"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
            ));

        verify(commentService).getComment(1L);
    }

    @Test
    @DisplayName("사용자별 댓글 목록 조회 API가 정상적으로 동작해야 한다")
    void getCommentsByUser() throws Exception {
        // given
        Long userId = 1L;
        
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo(1L, "testuser", "테스트유저", null);
        CommentResponse comment = new CommentResponse(
            1L,
            "정말 좋은 문구입니다!",
            userInfo,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // 명시적인 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<CommentResponse> page = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentService.getCommentsByUser(eq(1L), any())).thenReturn(page);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/comments/users/{userId}", userId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].content").value("정말 좋은 문구입니다!"))
            .andDo(document("comment/get-comments-by-user",
                pathParameters(
                    parameterWithName("userId").description("사용자 ID")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                .andWithPrefix("data.content[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("quoteId").type(JsonFieldType.NUMBER).description("인용구 ID"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("작성자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("작성자 아이디"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("user.profileImage").type(JsonFieldType.NULL).description("프로필 이미지 URL"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
                .and(RestDocsUtils.getPageResponseFields())
            ));

        verify(commentService).getCommentsByUser(eq(1L), any());
    }

    @Test
    @DisplayName("문구별 댓글 목록 조회 API가 정상적으로 동작해야 한다")
    void getCommentsByQuote() throws Exception {
        // given
        Long quoteId = 1L;
        
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo(1L, "testuser", "테스트유저", null);
        CommentResponse comment = new CommentResponse(
            1L,
            "정말 좋은 문구입니다!",
            userInfo,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // 명시적인 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<CommentResponse> page = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentService.getCommentsByQuote(eq(1L), any())).thenReturn(page);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/comments/quotes/{quoteId}", quoteId)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].content").value("정말 좋은 문구입니다!"))
            .andDo(document("comment/get-comments-by-quote",
                pathParameters(
                    parameterWithName("quoteId").description("인용구 ID")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                .andWithPrefix("data.content[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("quoteId").type(JsonFieldType.NUMBER).description("인용구 ID"),
                    fieldWithPath("user").type(JsonFieldType.OBJECT).description("작성자 정보"),
                    fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                    fieldWithPath("user.username").type(JsonFieldType.STRING).description("작성자 아이디"),
                    fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("user.profileImage").type(JsonFieldType.NULL).description("프로필 이미지 URL"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 시간")
                )
                .and(RestDocsUtils.getPageResponseFields())
            ));

        verify(commentService).getCommentsByQuote(eq(1L), any());
    }
} 