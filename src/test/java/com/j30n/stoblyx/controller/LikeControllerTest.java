package com.j30n.stoblyx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.StoblyxApplication;
import com.j30n.stoblyx.config.RestDocsConfig;
import com.j30n.stoblyx.config.TestRedisConfig;
import com.j30n.stoblyx.config.TestSecurityConfig;
import com.j30n.stoblyx.config.TestDataConfig;
import com.j30n.stoblyx.config.TestKoBartConfig;
import com.j30n.stoblyx.config.TestControllerAdvice;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Like;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@SpringBootTest(
    classes = {
        StoblyxApplication.class,
        TestSecurityConfig.class,
        TestRedisConfig.class,
        TestDataConfig.class,
        TestKoBartConfig.class,
        TestControllerAdvice.class
    }
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfig.class)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.redis.enabled=true",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "spring.data.redis.password=",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
            "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "kobart.api.url=http://localhost:5000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
    }
)
@Transactional
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BookRepository bookRepository;

    private User testUser;
    private Quote testQuote;
    private static long testCounter = 0;

    @BeforeEach
    void setUp() {
        log.debug("테스트 데이터 초기화 시작");

        // 테스트 데이터 초기화
        likeRepository.deleteAll();
        quoteRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        // 테스트 사용자 생성
        testUser = User.builder()
            .username("testUser" + testCounter)
            .email("test" + testCounter + "@example.com")
            .password("password")
            .nickname("테스트 사용자" + testCounter)
            .role(UserRole.USER)
            .build();
        userRepository.save(testUser);
        log.debug("테스트 사용자 생성 완료: {}", testUser);

        // 테스트 책 생성
        Book testBook = Book.builder()
            .title("테스트 책" + testCounter)
            .author("테스트 작가" + testCounter)
            .isbn("979-11-" + String.format("%05d", testCounter + 3000) + "-11-5")
            .description("테스트 설명입니다.")
            .publisher("테스트 출판사")
            .publishDate(LocalDate.now())
            .genres(List.of("소설", "에세이"))
            .build();
        bookRepository.save(testBook);

        // 테스트 문구 생성
        testQuote = Quote.builder()
            .content("테스트 문구입니다.")
            .user(testUser)
            .book(testBook)
            .page(42)
            .build();
        quoteRepository.save(testQuote);
        log.debug("테스트 문구 생성 완료: {}", testQuote);

        testCounter++;
        log.debug("테스트 데이터 초기화 완료");
    }

    @Test
    void 문구_좋아요_테스트() throws Exception {
        log.debug("문구 좋아요 테스트 시작");

        // when & then
        MvcResult result = mockMvc.perform(post("/likes/quotes/{quoteId}", testQuote.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())  // 요청/응답 상세 정보 출력
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(true))
            .andDo(document("like-quote",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("quoteId").description("좋아요할 문구 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("API 결과 상태"),
                    fieldWithPath("message").description("결과 메시지"),
                    fieldWithPath("data").description("좋아요 성공 여부 (true)")
                )
            ))
            .andReturn();

        log.debug("응답 결과: {}", result.getResponse().getContentAsString());
        log.debug("문구 좋아요 테스트 종료");
    }

    @Test
    void 문구_좋아요_취소_테스트() throws Exception {
        log.debug("문구 좋아요 취소 테스트 시작");

        // given
        Like like = Like.builder()
            .user(testUser)
            .quote(testQuote)
            .build();
        likeRepository.save(like);
        log.debug("테스트용 좋아요 데이터 생성: {}", like);

        // when & then
        MvcResult result = mockMvc.perform(delete("/likes/quotes/{quoteId}", testQuote.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(false))
            .andDo(document("unlike-quote",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("quoteId").description("좋아요 취소할 문구 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("API 결과 상태"),
                    fieldWithPath("message").description("결과 메시지"),
                    fieldWithPath("data").description("좋아요 취소 성공 여부 (false)")
                )
            ))
            .andReturn();

        log.debug("응답 결과: {}", result.getResponse().getContentAsString());
        log.debug("문구 좋아요 취소 테스트 종료");
    }

    @Test
    void 문구_좋아요_상태_확인_테스트() throws Exception {
        log.debug("문구 좋아요 상태 확인 테스트 시작");

        // given
        Like like = Like.builder()
            .user(testUser)
            .quote(testQuote)
            .build();
        likeRepository.save(like);
        log.debug("테스트용 좋아요 데이터 생성: {}", like);

        // when & then
        MvcResult result = mockMvc.perform(get("/likes/quotes/{quoteId}/status", testQuote.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(true))
            .andDo(document("check-like-status",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("quoteId").description("좋아요 상태를 확인할 문구 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("API 결과 상태"),
                    fieldWithPath("message").description("결과 메시지"),
                    fieldWithPath("data").description("좋아요 상태 (true: 좋아요 상태, false: 좋아요하지 않은 상태)")
                )
            ))
            .andReturn();

        log.debug("응답 결과: {}", result.getResponse().getContentAsString());
        log.debug("문구 좋아요 상태 확인 테스트 종료");
    }

    @Test
    void 문구_좋아요_수_조회_테스트() throws Exception {
        log.debug("문구 좋아요 수 조회 테스트 시작");

        // given
        Like like = Like.builder()
            .user(testUser)
            .quote(testQuote)
            .build();
        likeRepository.save(like);
        log.debug("테스트용 좋아요 데이터 생성: {}", like);

        // when & then
        MvcResult result = mockMvc.perform(get("/likes/quotes/{quoteId}/count", testQuote.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(1))
            .andReturn();

        log.debug("응답 결과: {}", result.getResponse().getContentAsString());
        log.debug("문구 좋아요 수 조회 테스트 종료");
    }

    @Test
    void 사용자가_좋아요한_문구_ID_목록_조회_테스트() throws Exception {
        log.debug("사용자가 좋아요한 문구 ID 목록 조회 테스트 시작");

        // given
        Like like = Like.builder()
            .user(testUser)
            .quote(testQuote)
            .build();
        likeRepository.save(like);
        log.debug("테스트용 좋아요 데이터 생성: {}", like);

        // when & then
        mockMvc.perform(get("/likes/quotes")
                .with(SecurityMockMvcRequestPostProcessors.user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0]").value(testQuote.getId()))
            .andDo(document("get-liked-quotes",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("result").description("API 결과 상태"),
                    fieldWithPath("message").description("결과 메시지"),
                    fieldWithPath("data").description("사용자가 좋아요한 문구 ID 목록")
                )
            ));

        log.debug("사용자가 좋아요한 문구 ID 목록 조회 테스트 종료");
    }
}