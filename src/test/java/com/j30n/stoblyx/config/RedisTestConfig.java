package com.j30n.stoblyx.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableCaching
public class RedisTestConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> stringRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    
    @Bean(name = "cacheRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> cacheRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        
        // 키 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        
        // 값 직렬화 설정 (JSON)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // 해시 키/값 직렬화 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }
    
    @Bean
    @Primary
    public CacheManager cacheManager() {
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

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}