package com.j30n.stoblyx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.StoblyxApplication;
import com.j30n.stoblyx.config.RestDocsConfig;
import com.j30n.stoblyx.config.TestRedisConfig;
import com.j30n.stoblyx.config.TestSecurityConfig;
import com.j30n.stoblyx.config.TestDataConfig;
import com.j30n.stoblyx.config.TestKoBartConfig;
import com.j30n.stoblyx.domain.model.Comment;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.CommentRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import com.j30n.stoblyx.dto.CommentRequestDto;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(
    classes = {
        StoblyxApplication.class,
        TestSecurityConfig.class,
        TestRedisConfig.class,
        TestDataConfig.class,
        TestKoBartConfig.class
    }
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfig.class)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.redis.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
            "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
            "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
            "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "kobart.api.url=http://localhost:5000"
    },
    locations = "classpath:application-test.yml"
)
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User testUser;
    private Quote testQuote;
    private static long testCounter = 0;

    @BeforeEach
    void setUp() {
        log.debug("테스트 데이터 초기화 시작");

        // 테스트 데이터 초기화
        commentRepository.deleteAll();
        quoteRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성 (고유한 이메일 사용)
        testUser = User.builder()
            .username("testUser" + testCounter)
            .email("test" + testCounter + "@example.com")
            .password("password")
            .nickname("테스트 사용자" + testCounter)
            .role(UserRole.USER)
            .build();
        testCounter++;
        userRepository.save(testUser);
        log.debug("테스트 사용자 생성 완료: {}", testUser);

        // 테스트 문구 생성
        testQuote = Quote.builder()
            .content("테스트 문구입니다.")
            .user(testUser)
            .build();
        quoteRepository.save(testQuote);
        log.debug("테스트 문구 생성 완료: {}", testQuote);

        log.debug("테스트 데이터 초기화 완료");
    }

    @Test
    void 댓글_작성_테스트() throws Exception {
        log.debug("댓글 작성 테스트 시작");

        // given
        CommentRequestDto requestDto = new CommentRequestDto("테스트 댓글입니다.");
        log.debug("댓글 작성 요청 데이터: {}", requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/comments/quotes/{quoteId}", testQuote.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value(requestDto.content()))
            .andDo(document("create-comment",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("quoteId").description("댓글을 작성할 문구 ID")
                ),
                requestFields(
                    fieldWithPath("content").description("댓글 내용")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.id").description("댓글 ID"),
                    fieldWithPath("data.content").description("댓글 내용"),
                    fieldWithPath("data.userId").description("작성자 ID"),
                    fieldWithPath("data.username").description("작성자 이름"),
                    fieldWithPath("data.quoteId").description("문구 ID"),
                    fieldWithPath("data.createdAt").description("생성 시간"),
                    fieldWithPath("data.modifiedAt").description("수정 시간")
                )
            ));

        log.debug("댓글 작성 테스트 종료");
    }

    @Test
    void 댓글_조회_테스트() throws Exception {
        log.debug("댓글 조회 테스트 시작");

        // given
        Comment comment = Comment.builder()
            .content("테스트 댓글입니다.")
            .user(testUser)
            .quote(testQuote)
            .build();
        commentRepository.save(comment);
        log.debug("테스트 댓글 생성 완료: {}", comment);

        // when & then
        mockMvc.perform(get("/api/v1/comments/{commentId}", comment.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value(comment.getContent()))
            .andDo(document("get-comment",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("commentId").description("조회할 댓글 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.id").description("댓글 ID"),
                    fieldWithPath("data.content").description("댓글 내용"),
                    fieldWithPath("data.userId").description("작성자 ID"),
                    fieldWithPath("data.username").description("작성자 이름"),
                    fieldWithPath("data.quoteId").description("문구 ID"),
                    fieldWithPath("data.createdAt").description("생성 시간"),
                    fieldWithPath("data.modifiedAt").description("수정 시간")
                )
            ));

        log.debug("댓글 조회 테스트 종료");
    }

    @Test
    void 문구별_댓글_목록_조회_테스트() throws Exception {
        log.debug("문구별 댓글 목록 조회 테스트 시작");

        // given
        Comment comment = Comment.builder()
            .content("테스트 댓글입니다.")
            .user(testUser)
            .quote(testQuote)
            .build();
        commentRepository.save(comment);
        log.debug("테스트 댓글 생성 완료: {}", comment);

        // when & then
        mockMvc.perform(get("/api/v1/comments/quotes/{quoteId}", testQuote.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].content").value("테스트 댓글입니다."))
            .andDo(document("get-comments-by-quote",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("quoteId").description("댓글을 조회할 문구 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.content").description("댓글 목록"),
                    fieldWithPath("data.content[].id").description("댓글 ID"),
                    fieldWithPath("data.content[].content").description("댓글 내용"),
                    fieldWithPath("data.content[].userId").description("작성자 ID"),
                    fieldWithPath("data.content[].username").description("작성자 이름"),
                    fieldWithPath("data.content[].quoteId").description("문구 ID"),
                    fieldWithPath("data.content[].createdAt").description("생성 시간"),
                    fieldWithPath("data.content[].modifiedAt").description("수정 시간"),
                    fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                    fieldWithPath("data.pageable.sort.empty").description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").description("정렬 여부"),
                    fieldWithPath("data.pageable.offset").description("페이지 오프셋"),
                    fieldWithPath("data.pageable.paged").description("페이징 사용 여부"),
                    fieldWithPath("data.pageable.unpaged").description("페이징 미사용 여부"),
                    fieldWithPath("data.totalElements").description("전체 요소 수"),
                    fieldWithPath("data.totalPages").description("전체 페이지 수"),
                    fieldWithPath("data.last").description("마지막 페이지 여부"),
                    fieldWithPath("data.size").description("페이지 크기"),
                    fieldWithPath("data.number").description("현재 페이지 번호"),
                    fieldWithPath("data.sort.empty").description("정렬 여부"),
                    fieldWithPath("data.sort.sorted").description("정렬 여부"),
                    fieldWithPath("data.sort.unsorted").description("정렬 여부"),
                    fieldWithPath("data.first").description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                    fieldWithPath("data.empty").description("데이터 존재 여부")
                )
            ));

        log.debug("문구별 댓글 목록 조회 테스트 종료");
    }

    @Test
    void 사용자별_댓글_목록_조회_테스트() throws Exception {
        log.debug("사용자별 댓글 목록 조회 테스트 시작");

        // given
        Comment comment = Comment.builder()
            .content("테스트 댓글입니다.")
            .user(testUser)
            .quote(testQuote)
            .build();
        commentRepository.save(comment);
        log.debug("테스트 댓글 생성 완료: {}", comment);

        // when & then
        mockMvc.perform(get("/api/v1/comments/users/{userId}", testUser.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].content").value("테스트 댓글입니다."))
            .andDo(document("get-comments-by-user",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("userId").description("댓글을 조회할 사용자 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.content").description("댓글 목록"),
                    fieldWithPath("data.content[].id").description("댓글 ID"),
                    fieldWithPath("data.content[].content").description("댓글 내용"),
                    fieldWithPath("data.content[].userId").description("작성자 ID"),
                    fieldWithPath("data.content[].username").description("작성자 이름"),
                    fieldWithPath("data.content[].quoteId").description("문구 ID"),
                    fieldWithPath("data.content[].createdAt").description("생성 시간"),
                    fieldWithPath("data.content[].modifiedAt").description("수정 시간"),
                    fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                    fieldWithPath("data.pageable.sort.empty").description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").description("정렬 여부"),
                    fieldWithPath("data.pageable.offset").description("페이지 오프셋"),
                    fieldWithPath("data.pageable.paged").description("페이징 사용 여부"),
                    fieldWithPath("data.pageable.unpaged").description("페이징 미사용 여부"),
                    fieldWithPath("data.totalElements").description("전체 요소 수"),
                    fieldWithPath("data.totalPages").description("전체 페이지 수"),
                    fieldWithPath("data.last").description("마지막 페이지 여부"),
                    fieldWithPath("data.size").description("페이지 크기"),
                    fieldWithPath("data.number").description("현재 페이지 번호"),
                    fieldWithPath("data.sort.empty").description("정렬 여부"),
                    fieldWithPath("data.sort.sorted").description("정렬 여부"),
                    fieldWithPath("data.sort.unsorted").description("정렬 여부"),
                    fieldWithPath("data.first").description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                    fieldWithPath("data.empty").description("데이터 존재 여부")
                )
            ));

        log.debug("사용자별 댓글 목록 조회 테스트 종료");
    }

    @Test
    void 댓글_수정_테스트() throws Exception {
        log.debug("댓글 수정 테스트 시작");

        // given
        Comment comment = Comment.builder()
            .content("테스트 댓글입니다.")
            .user(testUser)
            .quote(testQuote)
            .build();
        commentRepository.save(comment);
        log.debug("테스트 댓글 생성 완료: {}", comment);

        CommentRequestDto requestDto = new CommentRequestDto("수정된 댓글입니다.");
        log.debug("댓글 수정 요청 데이터: {}", requestDto);

        // when & then
        mockMvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").value(requestDto.content()))
            .andDo(document("update-comment",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("commentId").description("수정할 댓글 ID")
                ),
                requestFields(
                    fieldWithPath("content").description("수정할 댓글 내용")
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.id").description("댓글 ID"),
                    fieldWithPath("data.content").description("수정된 댓글 내용"),
                    fieldWithPath("data.userId").description("작성자 ID"),
                    fieldWithPath("data.username").description("작성자 이름"),
                    fieldWithPath("data.quoteId").description("문구 ID"),
                    fieldWithPath("data.createdAt").description("생성 시간"),
                    fieldWithPath("data.modifiedAt").description("수정 시간")
                )
            ));

        log.debug("댓글 수정 테스트 종료");
    }

    @Test
    void 댓글_삭제_테스트() throws Exception {
        log.debug("댓글 삭제 테스트 시작");

        // given
        Comment comment = Comment.builder()
            .content("삭제될 댓글입니다.")
            .user(testUser)
            .quote(testQuote)
            .build();
        commentRepository.save(comment);
        log.debug("테스트 댓글 생성 완료: {}", comment);

        // when & then
        mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(document("delete-comment",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("commentId").description("삭제할 댓글 ID")
                )
            ));

        // 삭제 확인
        mockMvc.perform(get("/api/v1/comments/{commentId}", comment.getId())
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        log.debug("댓글 삭제 테스트 종료");
    }
}