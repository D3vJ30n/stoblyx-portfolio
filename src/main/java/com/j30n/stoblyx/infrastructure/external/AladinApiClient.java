package com.j30n.stoblyx.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.BookInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 알라딘 API 클라이언트
 * 알라딘 OpenAPI를 사용하여 책 정보를 가져오는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AladinApiClient {

    private final RestTemplate commonRestTemplate;
    private final ObjectMapper objectMapper;
    private final AladinApiProperties properties;
    
    private static final int MAX_ITEMS_PER_PAGE = 50; // 알라딘 API의 페이지당 최대 가져올 수 있는 아이템 수
    private static final int MAX_TOTAL_ITEMS = 200;   // 최대 가져올 총 아이템 수

    /**
     * 신간 리스트를 가져옵니다.
     * 최신 출간된 책을 200개까지 가져옵니다.
     */
    public List<Book> getNewBooks() {
        log.info("알라딘 API에서 신간 도서 목록을 가져옵니다. API 키: {}", properties.getTtbKey());
        String apiUrl = buildApiUrl("ItemNewAll", null, 10, 1);
        log.info("Direct Test API URL: {}", apiUrl);
        try {
            // 직접 호출 테스트
            ResponseEntity<String> directResponse = commonRestTemplate.getForEntity(apiUrl, String.class);
            log.info("직접 호출 응답 상태 코드: {}", directResponse.getStatusCode());
            log.info("직접 호출 응답 본문: {}", directResponse.getBody());
        } catch (Exception e) {
            log.error("직접 API 호출 실패: {}", e.getMessage(), e);
        }
        
        return fetchBooksWithPagination("ItemNewAll", null, MAX_TOTAL_ITEMS);
    }

    /**
     * 베스트셀러 리스트를 가져옵니다.
     * 현재 인기있는 책을 200개까지 가져옵니다.
     */
    public List<Book> getBestSellers() {
        log.info("알라딘 API에서 베스트셀러 목록을 가져옵니다. API 키: {}", properties.getTtbKey());
        return fetchBooksWithPagination("Bestseller", null, MAX_TOTAL_ITEMS);
    }

    /**
     * 키워드로 책을 검색합니다.
     * 검색 결과에서 200개까지 가져옵니다.
     */
    public List<Book> searchBooks(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("Query(검색어)는 필수 파라미터입니다.");
        }
        
        if (!StringUtils.hasText(properties.getTtbKey())) {
            throw new IllegalArgumentException("TTBKey는 필수 파라미터입니다.");
        }
        
        log.info("알라딘 API에서 키워드 '{}'로 책을 검색합니다. API 키: {}", keyword, properties.getTtbKey());
        log.info("검색 API URL: {}", properties.getSearchApiUrl());
        
        // 검색 URL을 직접 구성하여 로그로 출력
        String testSearchUrl = UriComponentsBuilder
            .fromHttpUrl(properties.getSearchApiUrl())
            .queryParam("TTBKey", properties.getTtbKey())
            .queryParam("QueryType", "Keyword")
            .queryParam("Query", keyword)
            .queryParam("MaxResults", 10)
            .queryParam("start", 1)
            .queryParam("SearchTarget", "Book")
            .queryParam("output", "js")
            .queryParam("Version", "20131101")
            .queryParam("Cover", "Big")
            .build()
            .toString();
        
        log.info("테스트 검색 URL: {}", testSearchUrl);
        
        // 검색 API는 별도의 URL을 사용
        return fetchBooksWithSearchApi("Keyword", keyword, MAX_TOTAL_ITEMS);
    }
    
    /**
     * ISBN으로 책 상세 정보를 조회합니다.
     */
    public Book getBookDetailByIsbn(String isbn13) {
        log.info("ISBN으로 책 상세 정보 조회: {}", isbn13);
        
        if (!StringUtils.hasText(isbn13)) {
            throw new IllegalArgumentException("ItemId(ISBN13)은 필수 파라미터입니다.");
        }
        
        if (!StringUtils.hasText(properties.getTtbKey())) {
            throw new IllegalArgumentException("TTBKey는 필수 파라미터입니다.");
        }
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(properties.getItemApiUrl())
                    .queryParam("TTBKey", properties.getTtbKey())
                    .queryParam("ItemId", isbn13)
                    .queryParam("ItemIdType", "ISBN13")
                    .queryParam("output", "js")
                    .queryParam("Version", "20131101")
                    .queryParam("Cover", "Big")
                    .build()
                    .toString();
            
            log.info("책 상세 정보 API URL: {}", url);
            
            ResponseEntity<Map<String, Object>> response = commonRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            log.info("책 상세 정보 API 응답 상태 코드: {}", response.getStatusCode());
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                log.warn("책 상세 정보 API 응답 본문이 null입니다.");
                return null;
            }
            
            log.debug("책 상세 정보 API 응답 본문: {}", responseBody);
            
            if (responseBody.containsKey("item")) {
                Object itemObj = responseBody.get("item");
                if (itemObj instanceof List<?>) {
                    List<?> itemList = (List<?>) itemObj;
                    if (!itemList.isEmpty() && itemList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> items = (List<Map<String, Object>>) itemList;
                        if (!items.isEmpty()) {
                            return convertToBook(items.get(0));
                        }
                    }
                }
            }
            
            log.warn("ISBN에 해당하는 책을 찾을 수 없습니다: {}", isbn13);
            return null;
        } catch (Exception e) {
            log.error("책 상세 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
    
    // API URL 빌드 유틸리티 메서드
    private String buildApiUrl(String queryType, String query, int maxResults, int start) {
        if (queryType == null || queryType.isEmpty()) {
            throw new IllegalArgumentException("QueryType은 필수 파라미터입니다.");
        }
        
        if (properties.getTtbKey() == null || properties.getTtbKey().isEmpty()) {
            throw new IllegalArgumentException("TTBKey는 필수 파라미터입니다.");
        }
        
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getApiUrl())
            .queryParam("TTBKey", properties.getTtbKey())
            .queryParam("QueryType", queryType)
            .queryParam("MaxResults", maxResults)
            .queryParam("start", start)
            .queryParam("SearchTarget", "Book")
            .queryParam("output", "js")
            .queryParam("Version", "20131101")
            .queryParam("Cover", "Big");
            
        if (StringUtils.hasText(query)) {
            builder.queryParam("Query", query);
        }
        
        return builder.build().toString();
    }
    
    /**
     * 페이지네이션을 적용하여 여러 페이지에 걸쳐 책 정보를 가져옵니다.
     * @param queryType 쿼리 타입 (예: ItemNewAll, Bestseller, ItemSearch)
     * @param query 검색어 (검색 시에만 사용, 다른 경우 null)
     * @param maxItems 최대 가져올 아이템 수
     * @return 책 객체 리스트
     */
    private List<Book> fetchBooksWithPagination(String queryType, String query, int maxItems) {
        List<Book> allBooks = new ArrayList<>();
        int currentPage = 1;
        int totalItemsToFetch = Math.min(maxItems, MAX_TOTAL_ITEMS);
        int itemsPerPage = MAX_ITEMS_PER_PAGE;
        
        // 첫 페이지만 가져와서 테스트 (페이지네이션 과정에서 오류가 발생할 수 있음)
        try {
            Map<String, Object> response = executeApiCall(queryType, query, currentPage, 10); // 10개만 가져오기
            log.info("첫 페이지 API 응답: {}", response);
            
            List<Book> booksFromCurrentPage = processBooksFromResponse(response);
            log.info("첫 페이지에서 가져온 책 수: {}", booksFromCurrentPage.size());
            
            allBooks.addAll(booksFromCurrentPage);
            
            // 이 부분은 원래 코드대로 페이지네이션 로직 수행
            if (!booksFromCurrentPage.isEmpty() && allBooks.size() < totalItemsToFetch) {
                currentPage++;
                
                while (allBooks.size() < totalItemsToFetch) {
                    int remainingItems = totalItemsToFetch - allBooks.size();
                    int itemsToFetch = Math.min(remainingItems, itemsPerPage);
                    
                    try {
                        response = executeApiCall(queryType, query, currentPage, itemsToFetch);
                        booksFromCurrentPage = processBooksFromResponse(response);
                        
                        if (booksFromCurrentPage.isEmpty()) {
                            // 더 이상 결과가 없으면 종료
                            break;
                        }
                        
                        allBooks.addAll(booksFromCurrentPage);
                        log.info("페이지 {}에서 {}개의 책을 가져왔습니다. 현재까지 총 {}개", 
                                 currentPage, booksFromCurrentPage.size(), allBooks.size());
                        
                        currentPage++;
                        
                        // API 호출 간 짧은 지연 추가 (API 서버 부하 방지)
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.error("API 호출 중 오류 발생 (페이지: {}): {}", currentPage, e.getMessage(), e);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("첫 페이지 API 호출 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("총 {}개의 책을 가져왔습니다.", allBooks.size());
        return allBooks;
    }
    
    /**
     * 검색 API를 사용하여 책을 검색하고 페이지네이션을 적용합니다.
     * @param queryType 쿼리 타입 (예: Keyword, Title, Author, Publisher)
     * @param query 검색어
     * @param maxItems 최대 가져올 아이템 수
     * @return 책 객체 리스트
     */
    private List<Book> fetchBooksWithSearchApi(String queryType, String query, int maxItems) {
        List<Book> allBooks = new ArrayList<>();
        int currentPage = 1;
        int totalItemsToFetch = Math.min(maxItems, MAX_TOTAL_ITEMS);
        int itemsPerPage = MAX_ITEMS_PER_PAGE;
        
        try {
            // 첫 페이지 호출
            log.info("첫 페이지 검색 API 호출 시작: 타입={}, 검색어={}", queryType, query);
            Map<String, Object> response = executeSearchApiCall(queryType, query, currentPage, 10); // 10개만 가져오기
            
            // API 응답에 totalResults가 있는지 확인
            if (response.containsKey("totalResults")) {
                int totalResults = Integer.parseInt(response.get("totalResults").toString());
                log.info("검색 API 응답의 총 결과 수: {}", totalResults);
                
                if (totalResults > 0) {
                    log.info("결과가 있지만 변환 과정에서 손실될 수 있습니다.");
                }
            }
            
            log.info("첫 페이지 API 응답 키 확인: {}", response.keySet());
            
            if (response.containsKey("item")) {
                Object itemObj = response.get("item");
                if (itemObj instanceof List<?>) {
                    List<?> itemList = (List<?>) itemObj;
                    if (!itemList.isEmpty() && itemList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> items = (List<Map<String, Object>>) itemList;
                        log.info("API 응답의 아이템 수: {}", items.size());
                        
                        // 첫 번째 아이템을 직접 Book으로 변환 시도
                        if (!items.isEmpty()) {
                            Map<String, Object> firstItem = items.get(0);
                            log.info("첫 번째 아이템의 키: {}", firstItem.keySet());
                            log.info("첫 번째 아이템의 title: {}, author: {}, isbn13: {}", 
                                    firstItem.get("title"), 
                                    firstItem.get("author"), 
                                    firstItem.get("isbn13"));
                            
                            try {
                                Book book = convertToBook(firstItem);
                                log.info("첫 번째 아이템 변환 성공: {}", book.toString());
                            } catch (Exception e) {
                                log.error("첫 번째 아이템 변환 실패: {}", e.getMessage(), e);
                            }
                        }
                    }
                }
            } else {
                log.warn("API 응답에 item 필드가 없습니다.");
            }
            
            List<Book> booksFromCurrentPage = processBooksFromResponse(response);
            log.info("검색 첫 페이지에서 가져온 책 수: {}", booksFromCurrentPage.size());
            
            allBooks.addAll(booksFromCurrentPage);
            
            // 이 부분은 원래 코드대로 페이지네이션 로직 수행
            if (!booksFromCurrentPage.isEmpty() && allBooks.size() < totalItemsToFetch) {
                currentPage++;
                
                while (allBooks.size() < totalItemsToFetch) {
                    int remainingItems = totalItemsToFetch - allBooks.size();
                    int itemsToFetch = Math.min(remainingItems, itemsPerPage);
                    
                    try {
                        response = executeSearchApiCall(queryType, query, currentPage, itemsToFetch);
                        booksFromCurrentPage = processBooksFromResponse(response);
                        
                        if (booksFromCurrentPage.isEmpty()) {
                            // 더 이상 결과가 없으면 종료
                            break;
                        }
                        
                        allBooks.addAll(booksFromCurrentPage);
                        log.info("검색 페이지 {}에서 {}개의 책을 가져왔습니다. 현재까지 총 {}개", 
                                 currentPage, booksFromCurrentPage.size(), allBooks.size());
                        
                        currentPage++;
                        
                        // API 호출 간 짧은 지연 추가 (API 서버 부하 방지)
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.error("검색 API 호출 중 오류 발생 (페이지: {}): {}", currentPage, e.getMessage(), e);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("검색 첫 페이지 API 호출 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("검색 결과로 총 {}개의 책을 가져왔습니다.", allBooks.size());
        return allBooks;
    }
    
    /**
     * 알라딘 API를 호출하여 책 정보를 가져옵니다.
     */
    private Map<String, Object> executeApiCall(String queryType, String query, int page, int maxResults) {
        log.info("알라딘 API 호출: 타입={}, 검색어={}, 페이지={}, 최대결과수={}", 
                 queryType, query, page, maxResults);
        
        // 필수 파라미터 검증
        if (queryType == null || queryType.isEmpty()) {
            throw new IllegalArgumentException("QueryType은 필수 파라미터입니다.");
        }
        
        if (properties.getTtbKey() == null || properties.getTtbKey().isEmpty()) {
            throw new IllegalArgumentException("TTBKey는 필수 파라미터입니다.");
        }
        
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getApiUrl())
            .queryParam("TTBKey", properties.getTtbKey())
            .queryParam("QueryType", queryType)
            .queryParam("MaxResults", maxResults)
            .queryParam("start", (page - 1) * maxResults + 1)  // 페이지 번호에서 시작 아이템 인덱스 계산
            .queryParam("SearchTarget", "Book")
            .queryParam("output", "js")
            .queryParam("Version", "20131101")
            .queryParam("Cover", "Big");  // 커버 이미지 큰 버전 요청
            
        if (StringUtils.hasText(query)) {
            builder.queryParam("Query", query);
        }
        
        String apiUrl = builder.build().toString();
        log.info("API URL: {}", apiUrl);
        
        try {
            ResponseEntity<Map<String, Object>> response = commonRestTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            log.info("API 응답 상태 코드: {}", response.getStatusCode());
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                log.warn("API 응답 본문이 null입니다.");
                return java.util.Collections.emptyMap();
            }
            
            // 에러 응답 확인
            if (responseBody.containsKey("errorCode")) {
                log.error("API 오류 응답: 코드={}, 메시지={}", 
                          responseBody.get("errorCode"), 
                          responseBody.get("errorMessage"));
                return java.util.Collections.emptyMap();
            }
            
            // totalResults와 같은 메타 정보 로깅
            if (responseBody.containsKey("totalResults")) {
                log.info("총 결과 수: {}", responseBody.get("totalResults"));
            }
            
            return responseBody;
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage(), e);
            return java.util.Collections.emptyMap();
        }
    }

    /**
     * 알라딘 검색 API를 호출하여 책 정보를 가져옵니다.
     */
    private Map<String, Object> executeSearchApiCall(String queryType, String query, int page, int maxResults) {
        log.info("알라딘 검색 API 호출: 타입={}, 검색어={}, 페이지={}, 최대결과수={}", 
                 queryType, query, page, maxResults);
        
        // 필수 파라미터 검증
        if (queryType == null || queryType.isEmpty()) {
            throw new IllegalArgumentException("QueryType은 필수 파라미터입니다.");
        }
        
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query(검색어)는 필수 파라미터입니다.");
        }
        
        if (properties.getTtbKey() == null || properties.getTtbKey().isEmpty()) {
            throw new IllegalArgumentException("TTBKey는 필수 파라미터입니다.");
        }
        
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getSearchApiUrl()) // 검색 API URL 사용
            .queryParam("TTBKey", properties.getTtbKey())
            .queryParam("QueryType", queryType)
            .queryParam("Query", query) // 검색어는 필수
            .queryParam("MaxResults", maxResults)
            .queryParam("start", (page - 1) * maxResults + 1)
            .queryParam("SearchTarget", "Book")
            .queryParam("output", "js")
            .queryParam("Version", "20131101")
            .queryParam("Cover", "Big");
        
        String apiUrl = builder.build().toString();
        log.info("검색 API URL: {}", apiUrl);
        
        try {
            // 직접 URL을 호출하여 테스트
            try {
                ResponseEntity<String> directResponse = commonRestTemplate.getForEntity(apiUrl, String.class);
                log.info("직접 호출 응답 상태 코드: {}", directResponse.getStatusCode());
                log.info("직접 호출 응답 본문: {}", directResponse.getBody());
            } catch (Exception e) {
                log.error("직접 API 호출 실패: {}", e.getMessage(), e);
            }
            
            ResponseEntity<Map<String, Object>> response = commonRestTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            log.info("검색 API 응답 상태 코드: {}", response.getStatusCode());
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                log.warn("검색 API 응답 본문이 null입니다.");
                return java.util.Collections.emptyMap();
            }
            
            // 전체 응답 본문 로깅
            log.info("검색 API 응답 전체 본문: {}", objectMapper.writeValueAsString(responseBody));
            
            // 에러 응답 확인
            if (responseBody.containsKey("errorCode")) {
                log.error("검색 API 오류 응답: 코드={}, 메시지={}", 
                          responseBody.get("errorCode"), 
                          responseBody.get("errorMessage"));
                return java.util.Collections.emptyMap();
            }
            
            // totalResults와 같은 메타 정보 로깅
            if (responseBody.containsKey("totalResults")) {
                int totalResults = Integer.parseInt(responseBody.get("totalResults").toString());
                log.info("검색 총 결과 수: {}", totalResults);
                
                // 결과는 있지만 item 필드가 없는 경우를 처리
                if (totalResults > 0 && !responseBody.containsKey("item")) {
                    log.warn("검색 결과가 있지만(totalResults={}) item 필드가 없습니다", totalResults);
                    log.info("API 응답 전체: {}", objectMapper.writeValueAsString(responseBody));
                    
                    // 응답 본문에 실제 데이터가 있는지 확인
                    for (String key : responseBody.keySet()) {
                        log.info("응답 필드: {} = {}", key, responseBody.get(key));
                    }
                }
            }
            
            return responseBody;
        } catch (Exception e) {
            log.error("검색 API 호출 중 예외 발생: {}", e.getMessage(), e);
            return java.util.Collections.emptyMap();
        }
    }

    /**
     * API 응답에서 책 정보 리스트를 추출합니다.
     */
    @SuppressWarnings("unchecked")
    private List<Book> processBooksFromResponse(Map<String, Object> response) {
        List<Book> books = new ArrayList<>();
        
        if (response == null || !response.containsKey("item")) {
            log.warn("API 응답에 item 필드가 없습니다: {}", response);
            return books;
        }
        
        Object itemObj = response.get("item");
        if (!(itemObj instanceof List<?>)) {
            log.warn("API 응답의 item 필드가 List 타입이 아닙니다: {}", itemObj);
            return books;
        }
        
        List<?> itemList = (List<?>) itemObj;
        if (itemList.isEmpty()) {
            log.warn("API 응답의 item 필드가 비어 있습니다: {}", response);
            return books;
        }
        
        if (!(itemList.get(0) instanceof Map)) {
            log.warn("API 응답의 item 요소가 Map 타입이 아닙니다: {}", itemList.get(0));
            return books;
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) itemList;
        
        log.info("API 응답에서 {}개의 아이템을 찾았습니다.", items.size());
        
        for (int i = 0; i < items.size(); i++) {
            try {
                Map<String, Object> item = items.get(i);
                log.info("아이템 {}의 정보: title={}, isbn13={}", i, item.get("title"), item.get("isbn13"));
                Book book = convertToBook(item);
                books.add(book);
                log.info("아이템 {}를 성공적으로 Book 객체로 변환했습니다.", i);
            } catch (Exception e) {
                log.error("책 정보 변환 중 오류 발생: {}, 스택 트레이스: {}", e.getMessage(), e.getStackTrace(), e);
            }
        }
        
        return books;
    }

    /**
     * API 응답의 개별 아이템을 Book 객체로 변환합니다.
     * 알라딘 API 응답은 camelCase와 소문자를 혼합해서 사용합니다.
     */
    @SuppressWarnings("unchecked")
    private Book convertToBook(Map<String, Object> item) {
        // 장르 정보 추출
        List<String> genres = new ArrayList<>();
        if (item.get("categoryName") != null) {
            String categoryName = (String) item.get("categoryName");
            String[] categories = categoryName.split(">");
            for (String category : categories) {
                genres.add(category.trim());
            }
        }
        
        // 출판일 파싱 - 'pubDate'가 아닌 'pubdate' 필드 사용
        LocalDate publishDate = null;
        if (item.get("pubDate") != null) {
            try {
                String pubDateStr = (String) item.get("pubDate");
                log.info("출판일 문자열: {}", pubDateStr);
                publishDate = LocalDate.parse(pubDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                log.warn("pubDate 출판일 파싱 오류: {}", item.get("pubDate"), e);
                // 다른 필드명(pubdate) 시도
                try {
                    if (item.get("pubdate") != null) {
                        String pubDateStr = (String) item.get("pubdate");
                        log.info("대체 출판일 문자열: {}", pubDateStr);
                        publishDate = LocalDate.parse(pubDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                } catch (Exception ex) {
                    log.warn("pubdate 출판일 파싱 오류: {}", item.get("pubdate"), ex);
                }
            }
        }
        
        // 출판년도 추출
        Integer publicationYear = null;
        if (publishDate != null) {
            publicationYear = publishDate.getYear();
        }
        
        // 정가, 판매가 추출 (소문자로 시작하는 필드명 사용)
        Integer priceStandard = parseIntegerSafely(item.get("priceStandard"));
        Integer priceSales = parseIntegerSafely(item.get("priceSales"));
        
        // API 응답 필드명이 일치하지 않는 경우 대안 필드명 확인
        if (priceStandard == null) {
            priceStandard = parseIntegerSafely(item.get("pricestandard"));
        }
        
        if (priceSales == null) {
            priceSales = parseIntegerSafely(item.get("pricesales"));
        }
        
        // 페이지 수 추출 - subInfo에서 가져옴
        Integer totalPages = null;
        String stockStatus = null;
        
        if (item.get("subInfo") != null) {
            Map<String, Object> subInfo = (Map<String, Object>) item.get("subInfo");
            if (subInfo.get("itemPage") != null) {
                totalPages = parseIntegerSafely(subInfo.get("itemPage"));
            }
            
            // 재고상태 정보
            if (subInfo.get("stockStatus") != null) {
                stockStatus = (String) subInfo.get("stockStatus");
            }
        }
        
        // 평점 추출
        Float customerReviewRank = null;
        if (item.get("customerReviewRank") != null) {
            try {
                customerReviewRank = Float.parseFloat(item.get("customerReviewRank").toString());
            } catch (NumberFormatException e) {
                log.warn("평점 파싱 오류: {}", item.get("customerReviewRank"), e);
            }
        }
        
        // 몰 타입 확인
        String mallType = (String) item.get("mallType");
        log.debug("상품 몰 타입: {}", mallType);
        
        // 상품 ID 처리
        String itemId = null;
        if (item.get("itemId") != null) {
            itemId = item.get("itemId").toString();
        }
        
        // 커버 이미지 URL
        String coverUrl = (String) item.get("cover");
        
        // BookInfo 생성
        BookInfo bookInfo = BookInfo.builder()
                .title((String) item.get("title"))
                .author((String) item.get("author"))
                .isbn((String) item.get("isbn"))
                .isbn13((String) item.get("isbn13"))
                .description((String) item.get("description"))
                .publisher((String) item.get("publisher"))
                .publishDate(publishDate)
                .thumbnailUrl(coverUrl)
                .cover(coverUrl)
                .genres(genres)
                .publicationYear(publicationYear)
                .totalPages(totalPages)
                .itemId(itemId)
                .priceStandard(priceStandard)
                .priceSales(priceSales)
                .categoryId(item.get("categoryId") != null ? item.get("categoryId").toString() : null)
                .categoryName((String) item.get("categoryName"))
                .link((String) item.get("link"))
                .adult(item.get("adult") != null ? item.get("adult").toString() : null)
                .customerReviewRank(customerReviewRank)
                .stockStatus(stockStatus)
                .mallType(mallType)
                .build();
                
        return new Book(bookInfo);
    }
    
    /**
     * 안전하게 Integer로 변환합니다.
     */
    private Integer parseIntegerSafely(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 오류: {}", value, e);
            return null;
        }
    }
} 