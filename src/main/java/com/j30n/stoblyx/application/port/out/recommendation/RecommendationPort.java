package com.j30n.stoblyx.application.port.out.recommendation;

import com.j30n.stoblyx.domain.model.PopularSearchTerm;
import com.j30n.stoblyx.domain.model.SearchTermProfile;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserSimilarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RecommendationPort {
    
    /**
     * 사용자의 검색어를 저장하거나 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param term 검색어
     * @return 저장된 사용자 검색어
     */
    SearchTermProfile saveSearchTermProfile(Long userId, String term);
    
    /**
     * 사용자의 모든 검색어를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 검색어 목록
     */
    List<SearchTermProfile> getUserSearchTerms(Long userId);
    
    /**
     * 사용자 유사도 정보를 저장합니다.
     *
     * @param sourceUserId 소스 사용자 ID
     * @param targetUserId 타겟 사용자 ID
     * @param similarityScore 유사도 점수
     * @return 저장된 사용자 유사도 정보
     */
    UserSimilarity saveUserSimilarity(Long sourceUserId, Long targetUserId, Double similarityScore);
    
    /**
     * 사용자의 유사도 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자 유사도 목록
     */
    Page<UserSimilarity> getUserSimilarities(Long userId, Pageable pageable);
    
    /**
     * 인기 검색어를 저장하거나 업데이트합니다.
     *
     * @param term 검색어
     * @param searchCount 검색 횟수
     * @param popularityScore 인기도 점수
     * @return 저장된 인기 검색어
     */
    PopularSearchTerm savePopularTerm(String term, Integer searchCount, Double popularityScore);
    
    /**
     * 인기 검색어 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 인기 검색어 목록
     */
    Page<PopularSearchTerm> getPopularTerms(Pageable pageable);
    
    /**
     * 최근 검색 기록을 조회합니다.
     *
     * @param since 기준 시간
     * @return 검색어와 검색 횟수 맵
     */
    Map<String, Integer> getRecentSearchCounts(LocalDateTime since);
    
    /**
     * 모든 사용자 목록을 조회합니다.
     *
     * @return 사용자 목록
     */
    List<User> getAllUsers();
} 