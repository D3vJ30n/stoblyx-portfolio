package com.j30n.stoblyx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.StoblyxApplication;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.config.*;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        TestControllerAdvice.class,
        TestRestTemplateConfig.class
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
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User testUser;
    private Book testBook;
    private static long testCounter = 0;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
            .username("testuser" + testCounter)
            .password("password")
            .nickname("테스트 사용자" + testCounter)
            .email("test" + testCounter + "@example.com")
            .role(UserRole.USER)
            .build();
        userRepository.save(testUser);

        // 테스트 책 생성
        testBook = Book.builder()
            .title("테스트 책" + testCounter)
            .author("테스트 작가" + testCounter)
            .isbn("979-11-" + String.format("%05d", testCounter + 1000) + "-11-5") // ISBN 중복 방지
            .description("테스트 설명입니다.")
            .publisher("테스트 출판사")
            .publishDate(LocalDate.now())
            .genres(List.of("소설", "에세이"))
            .build();
        bookRepository.save(testBook);

        testCounter++;
    }

    @Test
    @WithMockUser
    void 책_검색_테스트() throws Exception {
        log.debug("책 검색 테스트 시작");

        mockMvc.perform(get("/books")
                .param("searchKeyword", testBook.getTitle())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].title").value(testBook.getTitle()))
            .andDo(document("search-books",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("searchKeyword").description("검색어"),
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 기준").optional()
                )
            ));

        log.debug("책 검색 테스트 종료");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 책_생성_테스트() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
            "새로운 책",
            "새로운 작가",
            "979-11-00000-00-0",
            "새로운 책 설명",
            "새로운 출판사",
            LocalDate.now(),
            "https://example.com/book-thumbnail.jpg",
            List.of("소설", "에세이")
        );

        // when & then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value(request.title()))
            .andExpect(jsonPath("$.data.author").value(request.author()))
            .andDo(document("create-book",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 책_상세_조회_테스트() throws Exception {
        mockMvc.perform(get("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value(testBook.getTitle()))
            .andExpect(jsonPath("$.data.author").value(testBook.getAuthor()))
            .andDo(document("get-book",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 책_수정_테스트() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
            "수정된 책 제목",
            "수정된 작가",
            testBook.getIsbn(),
            "수정된 설명",
            "수정된 출판사",
            LocalDate.now(),
            "https://example.com/book-thumbnail-updated.jpg",
            List.of("소설", "시")
        );

        // when & then
        mockMvc.perform(put("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value(request.title()))
            .andExpect(jsonPath("$.data.author").value(request.author()))
            .andDo(document("update-book",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 책_삭제_테스트() throws Exception {
        mockMvc.perform(delete("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andDo(document("delete-book",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 존재하지_않는_책_조회_테스트() throws Exception {
        mockMvc.perform(get("/books/{id}", 999999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("get-book-not-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 잘못된_ISBN_형식으로_책_생성_테스트() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
            "잘못된 ISBN의 책",
            "작가",
            "잘못된-ISBN-형식",
            "설명",
            "출판사",
            LocalDate.now(),
            "https://example.com/book-thumbnail-invalid.jpg",
            List.of("소설")
        );

        // when & then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("create-book-invalid-isbn",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 페이징_테스트() throws Exception {
        // given
        int totalBooks = 15;
        for (int i = 0; i < totalBooks - 1; i++) { // testBook이 이미 하나 있으므로 14개만 추가
            Book book = Book.builder()
                .title("테스트 책 " + i)
                .author("테스트 작가 " + i)
                .isbn("979-11-" + String.format("%05d", i + 100) + "-11-5") // ISBN 중복 방지
                .description("테스트 설명입니다.")
                .publisher("테스트 출판사")
                .publishDate(LocalDate.now())
                .genres(List.of("소설", "에세이"))
                .build();
            bookRepository.save(book);
        }

        // when & then
        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "title,desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content", hasSize(5)))
            .andExpect(jsonPath("$.data.totalElements").value(totalBooks))
            .andExpect(jsonPath("$.data.totalPages").value(3))
            .andDo(document("books-pagination",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 장르별_검색_테스트() throws Exception {
        // given
        Book fantasyBook = Book.builder()
            .title("판타지 책")
            .author("판타지 작가")
            .isbn("979-11-" + String.format("%05d", 200) + "-11-5") // ISBN 중복 방지
            .description("판타지 장르의 책입니다.")
            .publisher("테스트 출판사")
            .publishDate(LocalDate.now())
            .genres(List.of("판타지", "모험"))
            .build();
        bookRepository.save(fantasyBook);

        // when & then
        mockMvc.perform(get("/books")
                .param("genre", "판타지")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].title").value("판타지 책"))
            .andDo(document("books-genre-search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void 권한없는_사용자_책_생성_테스트() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
            "새로운 책",
            "새로운 작가",
            "979-11-00000-00-0",
            "새로운 책 설명",
            "새로운 출판사",
            LocalDate.now(),
            "https://example.com/book-thumbnail.jpg",
            List.of("소설", "에세이")
        );

        // when & then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("인증되지 않은 사용자입니다."))
            .andExpect(jsonPath("$.data").isEmpty())
            .andDo(document("create-book-unauthorized",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 일반사용자_책_수정_시도_테스트() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
            "수정된 책 제목",
            "수정된 작가",
            testBook.getIsbn(),
            "수정된 설명",
            "수정된 출판사",
            LocalDate.now(),
            "https://example.com/book-thumbnail-updated.jpg",
            List.of("소설", "시")
        );

        // when & then
        mockMvc.perform(put("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("update-book-forbidden",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @WithMockUser
    void 일반사용자_책_삭제_시도_테스트() throws Exception {
        mockMvc.perform(delete("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andDo(document("delete-book-forbidden",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }
}