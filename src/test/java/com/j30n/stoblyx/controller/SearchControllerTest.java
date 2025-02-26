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
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.BookRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    private User testUser;
    private Book testBook;
    private Quote testQuote;
    private static long testCounter = 0;

    @BeforeEach
    void setUp() {
        log.debug("테스트 데이터 초기화 시작");

        // 테스트 데이터 초기화
        quoteRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

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
        testBook = Book.builder()
            .title("스프링 부트와 AWS로 혼자 구현하는 웹 서비스")
            .author("이동욱")
            .isbn("979-11-965781-0-" + testCounter)
            .description("프로그래밍 입문자를 위한 스프링 부트와 AWS로 웹 서비스를 구현하는 방법을 설명하는 책입니다.")
            .publisher("프리렉")
            .publishDate(LocalDateTime.now().toLocalDate())
            .genres(List.of("프로그래밍", "웹 개발"))
            .build();
        bookRepository.save(testBook);
        log.debug("테스트 책 생성 완료: {}", testBook);

        // 테스트 문구 생성
        testQuote = Quote.builder()
            .content("테스트와 깔끔한 코드는 프로그래머의 기본 소양이다.")
            .user(testUser)
            .book(testBook)
            .page(42) // page 필드 추가
            .build();
        quoteRepository.save(testQuote);
        log.debug("테스트 문구 생성 완료: {}", testQuote);

        testCounter++;
        log.debug("테스트 데이터 초기화 완료");
    }

    @Test
    void 통합_검색_테스트() throws Exception {
        log.debug("통합 검색 테스트 시작");

        // when & then
        mockMvc.perform(get("/search")
                .param("keyword", "프로그래")
                .param("type", "ALL")
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(2)))  // 책과 문구 각각 1개씩
            .andDo(document("search-all",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("type").description("검색 유형 (QUOTE, BOOK, ALL)").optional(),
                    parameterWithName("category").description("카테고리").optional(),
                    parameterWithName("sortBy").description("정렬 기준 (기본값: createdAt)").optional(),
                    parameterWithName("sortDirection").description("정렬 방향 (기본값: DESC)").optional(),
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 기준").optional()
                ),
                responseFields(
                    fieldWithPath("result").description("요청 처리 결과"),
                    fieldWithPath("message").description("요청 처리 메시지"),
                    fieldWithPath("data.content[]").description("검색 결과 목록"),
                    fieldWithPath("data.content[].id").description("검색 결과 ID"),
                    fieldWithPath("data.content[].type").description("검색 결과 유형 (QUOTE 또는 BOOK)"),
                    fieldWithPath("data.content[].title").description("제목"),
                    fieldWithPath("data.content[].content").description("내용"),
                    fieldWithPath("data.content[].author").description("저자"),
                    fieldWithPath("data.content[].category").description("카테고리"),
                    fieldWithPath("data.content[].createdAt").description("생성일"),
                    subsectionWithPath("data.pageable").description("페이징 정보"),
                    fieldWithPath("data.totalElements").description("전체 요소 수"),
                    fieldWithPath("data.totalPages").description("전체 페이지 수"),
                    fieldWithPath("data.last").description("마지막 페이지 여부"),
                    fieldWithPath("data.size").description("페이지 크기"),
                    fieldWithPath("data.number").description("현재 페이지 번호"),
                    subsectionWithPath("data.sort").description("정렬 정보"),
                    fieldWithPath("data.first").description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                    fieldWithPath("data.empty").description("데이터 존재 여부")
                )
            ));

        log.debug("통합 검색 테스트 종료");
    }

    @Test
    void 문구_검색_테스트() throws Exception {
        log.debug("문구 검색 테스트 시작");

        // when & then
        mockMvc.perform(get("/search")
                .param("keyword", "프로그래머")
                .param("type", "QUOTE")
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].type").value("QUOTE"))
            .andDo(document("search-quotes",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("type").description("검색 유형 (QUOTE)").optional(),
                    parameterWithName("category").description("카테고리").optional(),
                    parameterWithName("sortBy").description("정렬 기준 (기본값: createdAt)").optional(),
                    parameterWithName("sortDirection").description("정렬 방향 (기본값: DESC)").optional(),
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 기준").optional()
                )
            ));

        log.debug("문구 검색 테스트 종료");
    }

    @Test
    void 책_검색_테스트() throws Exception {
        log.debug("책 검색 테스트 시작");

        // when & then
        mockMvc.perform(get("/search")
                .param("keyword", "스프링")
                .param("type", "BOOK")
                .param("category", "프로그래밍")
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].type").value("BOOK"))
            .andDo(document("search-books",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("type").description("검색 유형 (BOOK)").optional(),
                    parameterWithName("category").description("카테고리").optional(),
                    parameterWithName("sortBy").description("정렬 기준 (기본값: createdAt)").optional(),
                    parameterWithName("sortDirection").description("정렬 방향 (기본값: DESC)").optional(),
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 기준").optional()
                )
            ));

        log.debug("책 검색 테스트 종료");
    }

    @Test
    void 검색어_누락_테스트() throws Exception {
        log.debug("검색어 누락 테스트 시작");

        // when & then
        mockMvc.perform(get("/search")
                .with(user(UserPrincipal.create(testUser)))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("search-without-keyword",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));

        log.debug("검색어 누락 테스트 종료");
    }
} 