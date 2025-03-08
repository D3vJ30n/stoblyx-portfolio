package com.j30n.stoblyx.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Quote API 통합 테스트 클래스
 */
@DisplayName("Quote API 통합 테스트")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class QuoteApiTest extends BaseApiTest {

    private static final String QUOTE_API_PATH = "/quotes";
    private static final String BOOK_API_PATH = "/books";

    @Test
    @DisplayName("문구 목록 조회 API 테스트")
    public void testGetQuotes() {
        given(requestSpec)
            .when()
                .get(QUOTE_API_PATH)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.content", instanceOf(Iterable.class));
    }

    @Test
    @DisplayName("문구 상세 조회 API 테스트")
    public void testGetQuote() {
        // 먼저 문구 목록을 조회하여 첫 번째 문구의 ID를 가져옴
        Integer quoteId = given(requestSpec)
                .when()
                    .get(QUOTE_API_PATH)
                .then()
                    .statusCode(200)
                    .extract()
                    .path("data.content[0].id");

        // ID가 없는 경우 테스트 데이터 생성
        if (quoteId == null) {
            quoteId = createTestQuote();
        }

        // 문구 상세 조회 테스트
        given(requestSpec)
            .when()
                .get(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", equalTo(quoteId))
                .body("data.content", notNullValue());
    }

    @Test
    @DisplayName("문구 등록 API 테스트")
    public void testCreateQuote() {
        // 먼저 책 목록을 조회하여 첫 번째 책의 ID를 가져옴
        Integer bookId = given(requestSpec)
                .when()
                    .get(BOOK_API_PATH)
                .then()
                    .statusCode(200)
                    .extract()
                    .path("data.content[0].id");

        // 책이 없는 경우 테스트 책 생성
        if (bookId == null) {
            bookId = createTestBook();
        }

        // 문구 등록 요청 데이터 생성
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "테스트 문구 내용입니다.");
        quoteData.put("page", 123);
        quoteData.put("bookId", bookId);

        // 인증된 요청으로 문구 등록
        given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(quoteData)
            .when()
                .post(QUOTE_API_PATH)
            .then()
                .statusCode(201)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", notNullValue())
                .body("data.content", equalTo("테스트 문구 내용입니다."))
                .body("data.page", equalTo(123))
                .body("data.bookId", equalTo(bookId));
    }

    @Test
    @DisplayName("문구 수정 API 테스트")
    public void testUpdateQuote() {
        // 테스트용 문구 생성
        Integer quoteId = createTestQuote();

        // 문구 수정 요청 데이터 생성
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("content", "수정된 문구 내용입니다.");
        updateData.put("page", 456);

        // 인증된 요청으로 문구 수정
        given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(updateData)
            .when()
                .put(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", equalTo(quoteId))
                .body("data.content", equalTo("수정된 문구 내용입니다."))
                .body("data.page", equalTo(456));
    }

    @Test
    @DisplayName("문구 삭제 API 테스트")
    public void testDeleteQuote() {
        // 테스트용 문구 생성
        Integer quoteId = createTestQuote();

        // 인증된 요청으로 문구 삭제
        given(givenAuth(userToken))
            .when()
                .delete(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .statusCode(204);

        // 삭제된 문구 조회 시 404 응답 확인
        given(requestSpec)
            .when()
                .get(QUOTE_API_PATH + "/" + quoteId)
            .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("인증 없이 문구 등록 시 실패 테스트")
    public void testCreateQuoteWithoutAuth() {
        // 문구 등록 요청 데이터 생성
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "테스트 문구 내용입니다.");
        quoteData.put("page", 123);
        quoteData.put("bookId", 1);

        // 인증 없이 문구 등록 요청
        given(requestSpec)
            .contentType(ContentType.JSON)
            .body(quoteData)
            .when()
                .post(QUOTE_API_PATH)
            .then()
                .statusCode(401); // 인증 실패 응답 코드
    }

    /**
     * 테스트용 문구 데이터 생성 및 문구 ID 반환
     */
    private Integer createTestQuote() {
        // 먼저 책 목록을 조회하여 첫 번째 책의 ID를 가져옴
        Integer bookId = given(requestSpec)
                .when()
                    .get(BOOK_API_PATH)
                .then()
                    .statusCode(200)
                    .extract()
                    .path("data.content[0].id");

        // 책이 없는 경우 테스트 책 생성
        if (bookId == null) {
            bookId = createTestBook();
        }

        // 문구 등록 요청 데이터 생성
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "테스트 문구 내용입니다.");
        quoteData.put("page", 123);
        quoteData.put("bookId", bookId);

        // 문구 등록하고 생성된 ID 반환
        return given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(quoteData)
            .when()
                .post(QUOTE_API_PATH)
            .then()
                .statusCode(201)
                .extract()
                .path("data.id");
    }

    /**
     * 테스트용 책 데이터 생성 및 책 ID 반환
     */
    private Integer createTestBook() {
        // 책 등록 요청 데이터 생성
        Map<String, String> bookData = new HashMap<>();
        bookData.put("title", "테스트 책 제목");
        bookData.put("author", "테스트 저자");
        bookData.put("description", "테스트 책 설명입니다.");
        bookData.put("coverImageUrl", "https://example.com/cover.jpg");

        // 책 등록하고 생성된 ID 반환
        return given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(bookData)
            .when()
                .post(BOOK_API_PATH)
            .then()
                .statusCode(201)
                .extract()
                .path("data.id");
    }
} 