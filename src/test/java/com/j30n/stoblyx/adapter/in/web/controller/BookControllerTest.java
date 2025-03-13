package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.service.book.BookService;
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
import org.springframework.data.domain.Sort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@DisplayName("도서 컨트롤러 테스트")
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
class BookControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookService bookService;

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
    }

    @Test
    @DisplayName("도서 상세 조회 API가 정상적으로 동작해야 한다")
    void getBookDetail() throws Exception {
        // given
        Long bookId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDate publishDate = LocalDate.of(2023, 1, 1);
        List<String> genres = Arrays.asList("소설", "판타지");

        BookResponse response = BookResponse.builder()
            .id(bookId)
            .title("테스트 도서")
            .author("테스트 작가")
            .publisher("테스트 출판사")
            .isbn("1234567890123")
            .thumbnailUrl("https://example.com/image.jpg")
            .description("도서 설명")
            .publicationYear(2023)
            .totalPages(300)
            .publishDate(publishDate)
            .genres(genres)
            .avgReadingTime(120)
            .averageRating(4.5)
            .ratingCount(10)
            .popularity(5)
            .createdAt(now)
            .modifiedAt(now)
            .deleted(false)
            .build();

        when(bookService.getBook(bookId)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/books/{bookId}", bookId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(bookId))
            .andExpect(jsonPath("$.data.title").value("테스트 도서"))
            .andDo(document("book/get-book-detail",
                pathParameters(
                    parameterWithName("bookId").description("도서 ID")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.",
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("도서 ID"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("도서 제목"),
                        fieldWithPath("author").type(JsonFieldType.STRING).description("저자"),
                        fieldWithPath("publisher").type(JsonFieldType.STRING).description("출판사"),
                        fieldWithPath("isbn").type(JsonFieldType.STRING).description("ISBN"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).optional().description("도서 썸네일 URL"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("도서 설명"),
                        fieldWithPath("genres").type(JsonFieldType.ARRAY).description("책 장르 목록"),
                        fieldWithPath("publicationYear").type(JsonFieldType.NUMBER).optional().description("출판 연도"),
                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).optional().description("총 페이지 수"),
                        fieldWithPath("publishDate").type(JsonFieldType.STRING).optional().description("출판일"),
                        fieldWithPath("avgReadingTime").type(JsonFieldType.NUMBER).optional().description("평균 독서 시간"),
                        fieldWithPath("averageRating").type(JsonFieldType.NUMBER).optional().description("평균 평점"),
                        fieldWithPath("ratingCount").type(JsonFieldType.NUMBER).optional().description("평점 수"),
                        fieldWithPath("popularity").type(JsonFieldType.NUMBER).optional().description("인기도"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).optional().description("등록일"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).optional().description("수정일"),
                        fieldWithPath("deleted").type(JsonFieldType.BOOLEAN).optional().description("삭제 여부")
                    )
            ));

        verify(bookService).getBook(bookId);
    }

    @Test
    @DisplayName("도서 목록 조회 API가 정상적으로 동작해야 한다")
    void getBooks() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDate publishDate = LocalDate.of(2023, 1, 1);
        List<String> genres1 = Arrays.asList("소설", "판타지");
        List<String> genres2 = Arrays.asList("역사", "에세이");

        List<BookResponse> books = List.of(
            BookResponse.builder()
                .id(1L)
                .title("테스트 도서 1")
                .author("테스트 작가 1")
                .publisher("테스트 출판사 1")
                .isbn("1234567890123")
                .thumbnailUrl("https://example.com/image1.jpg")
                .description("도서 설명 1")
                .publicationYear(2023)
                .totalPages(300)
                .publishDate(publishDate)
                .genres(genres1)
                .avgReadingTime(120)
                .averageRating(4.5)
                .ratingCount(10)
                .popularity(5)
                .createdAt(now)
                .modifiedAt(now)
                .deleted(false)
                .build(),
            BookResponse.builder()
                .id(2L)
                .title("테스트 도서 2")
                .author("테스트 작가 2")
                .publisher("테스트 출판사 2")
                .isbn("3210987654321")
                .thumbnailUrl("https://example.com/image2.jpg")
                .description("도서 설명 2")
                .publicationYear(2022)
                .totalPages(250)
                .publishDate(publishDate)
                .genres(genres2)
                .avgReadingTime(150)
                .averageRating(4.2)
                .ratingCount(8)
                .popularity(4)
                .createdAt(now)
                .modifiedAt(now)
                .deleted(false)
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageImpl<BookResponse> page = new PageImpl<>(books, pageable, books.size());

        when(bookService.getAllBooks(any())).thenReturn(page);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].id").value(1))
            .andDo(document("book/get-books",
                queryParameters(
                    parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                    parameterWithName("size").description("페이지 크기").optional(),
                    parameterWithName("sort").description("정렬 방식 (예: id,desc)").optional()
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].",
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("도서 ID"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("도서 제목"),
                        fieldWithPath("author").type(JsonFieldType.STRING).description("저자"),
                        fieldWithPath("publisher").type(JsonFieldType.STRING).description("출판사"),
                        fieldWithPath("isbn").type(JsonFieldType.STRING).description("ISBN"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).optional().description("도서 썸네일 URL"),
                        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("도서 설명"),
                        fieldWithPath("genres").type(JsonFieldType.ARRAY).description("책 장르 목록"),
                        fieldWithPath("publicationYear").type(JsonFieldType.NUMBER).optional().description("출판 연도"),
                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).optional().description("총 페이지 수"),
                        fieldWithPath("publishDate").type(JsonFieldType.STRING).optional().description("출판일"),
                        fieldWithPath("avgReadingTime").type(JsonFieldType.NUMBER).optional().description("평균 독서 시간"),
                        fieldWithPath("averageRating").type(JsonFieldType.NUMBER).optional().description("평균 평점"),
                        fieldWithPath("ratingCount").type(JsonFieldType.NUMBER).optional().description("평점 수"),
                        fieldWithPath("popularity").type(JsonFieldType.NUMBER).optional().description("인기도"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).optional().description("등록일"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).optional().description("수정일"),
                        fieldWithPath("deleted").type(JsonFieldType.BOOLEAN).optional().description("삭제 여부")
                    )
                    .and(
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부")
                    )
            ));

        verify(bookService).getAllBooks(any());
    }
} 