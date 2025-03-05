package com.j30n.stoblyx.adapter.out.persistence.recommendation;

import com.j30n.stoblyx.application.port.out.recommendation.RecommendationPort;
import com.j30n.stoblyx.domain.model.*;
import com.j30n.stoblyx.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class RecommendationPersistenceAdapter implements RecommendationPort {

    private final SearchTermProfileRepository searchTermProfileRepository;
    private final UserSimilarityRepository userSimilarityRepository;
    private final PopularSearchTermRepository popularSearchTermRepository;
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;
    
    @Override
    public SearchTermProfile saveSearchTermProfile(Long userId, String term) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        Optional<SearchTermProfile> existingTerm = searchTermProfileRepository.findByUserAndSearchTerm(user, term);
        
        if (existingTerm.isPresent()) {
            SearchTermProfile searchTermProfile = existingTerm.get();
            searchTermProfile.incrementFrequency();
            return searchTermProfileRepository.save(searchTermProfile);
        } else {
            SearchTermProfile newTerm = SearchTermProfile.builder()
                    .user(user)
                    .searchTerm(term)
                    .searchFrequency(1)
                    .termWeight(1.0)
                    .build();
            return searchTermProfileRepository.save(newTerm);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SearchTermProfile> getUserSearchTerms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        return searchTermProfileRepository.findByUser(user);
    }
    
    @Override
    public UserSimilarity saveUserSimilarity(Long sourceUserId, Long targetUserId, Double similarityScore) {
        User sourceUser = userRepository.findById(sourceUserId)
                .orElseThrow(() -> new IllegalArgumentException("소스 사용자를 찾을 수 없습니다: " + sourceUserId));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("타겟 사용자를 찾을 수 없습니다: " + targetUserId));
        
        Optional<UserSimilarity> existingSimilarity = 
                userSimilarityRepository.findBySourceUserAndTargetUser(sourceUser, targetUser);
        
        if (existingSimilarity.isPresent()) {
            UserSimilarity userSimilarity = existingSimilarity.get();
            userSimilarity.updateSimilarityScore(similarityScore);
            userSimilarity.activate();
            return userSimilarityRepository.save(userSimilarity);
        } else {
            UserSimilarity newSimilarity = UserSimilarity.builder()
                    .sourceUser(sourceUser)
                    .targetUser(targetUser)
                    .similarityScore(similarityScore)
                    .build();
            return userSimilarityRepository.save(newSimilarity);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserSimilarity> getUserSimilarities(Long userId, Pageable pageable) {
        return userSimilarityRepository.findActiveSimilaritiesForUser(userId, pageable);
    }
    
    @Override
    public PopularSearchTerm savePopularTerm(String term, Integer searchCount, Double popularityScore) {
        Optional<PopularSearchTerm> existingTerm = popularSearchTermRepository.findBySearchTerm(term);
        
        if (existingTerm.isPresent()) {
            PopularSearchTerm popularTerm = existingTerm.get();
            popularTerm.incrementSearchCount();
            popularTerm.updatePopularityScore(popularityScore);
            return popularSearchTermRepository.save(popularTerm);
        } else {
            PopularSearchTerm newTerm = PopularSearchTerm.builder()
                    .searchTerm(term)
                    .searchCount(searchCount)
                    .popularityScore(popularityScore)
                    .build();
            return popularSearchTermRepository.save(newTerm);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PopularSearchTerm> getPopularTerms(Pageable pageable) {
        return popularSearchTermRepository.findPopularTerms(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getRecentSearchCounts(LocalDateTime since) {
        // 최근 검색 기록 조회
        List<Search> recentSearches = searchRepository.findBySearchedAtBetween(since, LocalDateTime.now());
        
        // 검색어별 검색 횟수 집계
        Map<String, Integer> searchCounts = new HashMap<>();
        
        for (Search search : recentSearches) {
            String keyword = search.getKeyword();
            searchCounts.put(keyword, searchCounts.getOrDefault(keyword, 0) + 1);
        }
        
        return searchCounts;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
} 