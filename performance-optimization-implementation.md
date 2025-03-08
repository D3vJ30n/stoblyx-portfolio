# 성능 최적화 구현 내용

## 1. N+1 문제 해결

N+1 문제는 ORM을 사용할 때 발생하는 대표적인 성능 이슈로, 연관 엔티티를 조회할 때 불필요한 추가 쿼리가 발생하는 문제입니다. 이를 해결하기 위해 다음과 같은 최적화를 구현했습니다.

### 1.1 EntityGraph 적용

`@EntityGraph` 어노테이션을 사용하여 연관 엔티티를 함께 로딩하도록 구현했습니다.

```java
// QuoteRepository.java
@EntityGraph(attributePaths = {"user", "book"})
@Override
Optional<Quote> findById(Long id);

// BookRepository.java
@EntityGraph(attributePaths = {"genres"})
Optional<Book> findByIdAndIsDeletedFalse(Long id);
```

### 1.2 Fetch Join 활용

JPQL에서 `JOIN FETCH`를 사용하여 연관 엔티티를 함께 로딩하도록 구현했습니다.

```java
// QuoteRepository.java
@Query("SELECT DISTINCT q FROM Quote q " +
       "JOIN FETCH q.user " +
       "JOIN FETCH q.book b " +
       "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
       "AND q.isDeleted = false")
List<Quote> findByKeywordAndCategoryWithUserAndBook(
    @Param("keyword") String keyword,
    @Param("category") String category
);
```

### 1.3 카운트 쿼리 최적화

페이징 처리 시 카운트 쿼리를 최적화하여 불필요한 조인을 제거했습니다.

```java
// QuoteRepository.java
@Query(value = "SELECT q FROM Quote q " +
              "JOIN q.user u " +
              "JOIN q.book b " +
              "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
              "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
              "AND q.isDeleted = false",
       countQuery = "SELECT COUNT(q) FROM Quote q " +
                   "JOIN q.book b " +
                   "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
                   "AND q.isDeleted = false")
Page<Quote> findByKeywordAndCategoryOptimized(
    @Param("keyword") String keyword,
    @Param("category") String category,
    Pageable pageable
);
```

## 2. 페이징 및 인덱싱 최적화

대용량 데이터를 효율적으로 처리하기 위해 페이징 및 인덱싱 최적화를 구현했습니다.

### 2.1 키셋 기반 페이징 구현

오프셋 기반 페이징 대신 키셋 기반 페이징을 구현하여 대용량 데이터에서의 성능을 향상시켰습니다.

```java
// BookRepository.java
@Query("SELECT DISTINCT b FROM Book b " +
       "LEFT JOIN b.genres g " +
       "WHERE b.id > :lastId " +
       "AND (:keyword IS NULL OR " +
       "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:category IS NULL OR g = :category) " +
       "AND b.isDeleted = false " +
       "ORDER BY b.id ASC")
List<Book> findByKeywordAndCategoryWithKeyset(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("lastId") Long lastId,
        Pageable pageable);
```

### 2.2 복합 조건 키셋 페이징 구현

정렬 조건이 복합적인 경우에도 키셋 기반 페이징을 구현했습니다.

```java
// BookRepository.java
@Query("SELECT DISTINCT b FROM Book b " +
       "LEFT JOIN b.genres g " +
       "WHERE (b.popularity < :lastPopularity OR (b.popularity = :lastPopularity AND b.id > :lastId)) " +
       "AND (:keyword IS NULL OR " +
       "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:category IS NULL OR g = :category) " +
       "AND b.isDeleted = false " +
       "ORDER BY b.popularity DESC, b.id ASC")
List<Book> findByKeywordAndCategoryWithKeysetByPopularity(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("lastPopularity") Integer lastPopularity,
        @Param("lastId") Long lastId,
        Pageable pageable);
```

## 3. Redis 캐싱 구현

자주 조회되는 데이터의 성능을 향상시키기 위해 Redis 캐싱을 구현했습니다.

### 3.1 Redis 캐시 설정

Redis 캐시 매니저와 템플릿을 설정했습니다.

```java
// RedisCacheConfig.java
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(3600)) // 기본 TTL 1시간
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 사용자 캐시 (12시간)
        cacheConfigurations.put("userCache", defaultConfig.entryTtl(Duration.ofHours(12)));

        // 콘텐츠 캐시 (6시간)
        cacheConfigurations.put("contentCache", defaultConfig.entryTtl(Duration.ofHours(6)));

        // 설정 캐시 (24시간)
        cacheConfigurations.put("settingCache", defaultConfig.entryTtl(Duration.ofHours(24)));

        // 명언 캐시 (12시간)
        cacheConfigurations.put("quotesCache", defaultConfig.entryTtl(Duration.ofHours(12)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 설정 (JSON)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 해시 키/값 직렬화 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
```

### 3.2 서비스 메서드 캐싱

서비스 메서드에 캐싱 어노테이션을 적용했습니다.

```java
// QuoteService.java
@Override
@Transactional(readOnly = true)
@Cacheable(value = "quotesCache", key = "#quoteId", unless = "#result == null")
public QuoteResponse getQuote(Long quoteId, Long userId) {
    Quote quote = quotePort.findQuoteById(quoteId)
        .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));
    boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
    boolean isSaved = savedQuoteRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
    return QuoteResponse.from(quote, isLiked, isSaved);
}
```

### 3.3 캐시 무효화 구현

데이터가 변경될 때 캐시를 무효화하는 로직을 구현했습니다.

```java
// QuoteService.java
@Override
@Transactional
@Caching(evict = {
    @CacheEvict(value = "quotesCache", key = "#quoteId"),
    @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
})
public QuoteResponse updateQuote(Long quoteId, Long userId, QuoteUpdateRequest request) {
    // 업데이트 로직
}
```

## 4. JPA 쿼리 최적화

JPA 쿼리를 최적화하여 데이터베이스 부하를 줄였습니다.

### 4.1 읽기 전용 트랜잭션 활용

조회 전용 메서드에 `@Transactional(readOnly = true)`를 적용하여 영속성 컨텍스트 최적화를 구현했습니다.

```java
// QuoteService.java
@Override
@Transactional(readOnly = true)
@Cacheable(value = "quotesCache", key = "'user_' + #userId + '_page_' + #pageable.pageNumber", unless = "#result.isEmpty()")
public Page<QuoteResponse> getQuotes(Long userId, Pageable pageable) {
    // 조회 로직
}
```

## 5. 성능 개선 효과

위의 최적화를 통해 다음과 같은 성능 개선 효과를 기대할 수 있습니다:

1. **N+1 문제 해결**: 연관 엔티티 조회 시 발생하는 불필요한 쿼리를 줄여 응답 시간 단축
2. **키셋 기반 페이징**: 대용량 데이터에서 페이징 성능 향상 (특히 오프셋이 큰 경우)
3. **Redis 캐싱**: 자주 조회되는 데이터의 응답 시간 단축 및 데이터베이스 부하 감소
4. **JPA 쿼리 최적화**: 영속성 컨텍스트 최적화를 통한 메모리 사용량 감소 및 성능 향상

## 6. 추가 개선 사항

향후 다음과 같은 추가 개선을 고려할 수 있습니다:

1. **인덱스 최적화**: 자주 조회되는 컬럼에 인덱스 추가 및 복합 인덱스 활용
2. **벌크 연산 활용**: 대량 데이터 처리 시 벌크 연산 사용
3. **캐시 선행 로딩**: 자주 조회되는 데이터를 미리 캐시에 로딩
4. **모니터링 시스템 구축**: 성능 지표를 모니터링하여 지속적인 최적화 수행
