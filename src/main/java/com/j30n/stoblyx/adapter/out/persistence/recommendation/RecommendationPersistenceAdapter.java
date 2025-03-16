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
import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
public class RecommendationPersistenceAdapter implements RecommendationPort {

    private static final String ERROR_USER_NOT_FOUND = "사용자를 찾을 수 없습니다: ";

    private final SearchTermProfileRepository searchTermProfileRepository;
    private final UserSimilarityRepository userSimilarityRepository;
    private final PopularSearchTermRepository popularSearchTermRepository;
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    @Override
    public SearchTermProfile saveSearchTermProfile(Long userId, String term) {
        // userId가 유효한지 확인 (사용자가 존재하는지)
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND + userId));

        Optional<SearchTermProfile> existingTerm = searchTermProfileRepository.findBySearchTerm(term);

        if (existingTerm.isPresent()) {
            SearchTermProfile searchTermProfile = existingTerm.get();
            searchTermProfile.incrementSearchCount();
            return searchTermProfileRepository.save(searchTermProfile);
        } else {
            SearchTermProfile newTerm = SearchTermProfile.builder()
                .searchTerm(term)
                .searchCount(1)
                .build();
            return searchTermProfileRepository.save(newTerm);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchTermProfile> getUserSearchTerms(Long userId) {
        // 사용자의 최근 검색 기록에서 검색어 추출
        List<String> searchTerms = searchRepository.findTop10ByUserOrderByLastSearchedAtDesc(
                userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND + userId))
            )
            .stream()
            .map(Search::getSearchTerm)
            .distinct()
            .toList();

        // 검색어 하나씩 조회하여 리스트로 반환
        return searchTerms.stream()
            .map(term -> searchTermProfileRepository.findBySearchTerm(term).orElse(null))
            .filter(Objects::nonNull)
            .toList();
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
        List<Search> recentSearches = searchRepository.findByLastSearchedAtBetween(since, LocalDateTime.now());

        // 검색어별 검색 횟수 집계
        Map<String, Integer> searchCounts = new HashMap<>();

        for (Search search : recentSearches) {
            String searchTerm = search.getSearchTerm();
            searchCounts.put(searchTerm, searchCounts.getOrDefault(searchTerm, 0) + 1);
        }

        return searchCounts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        // 현재 인증된 사용자의 ID를 가져오는 로직
        // 실제 구현에서는 SecurityContextHolder 등을 사용하여 현재 인증된 사용자 정보를 가져옴
        // 테스트 목적으로 임시 구현
        return userRepository.findAll().stream()
            .findFirst()
            .map(User::getId)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSimilarity> getWeeklyRecommendation(Long userId) {
        // 사용자의 관심사와 검색 기록을 기반으로 주간 추천 정보를 생성하는 로직
        // 실제 구현에서는 사용자의 관심사, 검색 기록, 다른 사용자와의 유사도 등을 고려하여 추천 정보 생성
        // 테스트 목적으로 임시 구현
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND + userId));

        // 가장 유사도가 높은 사용자 찾기
        return userSimilarityRepository.findTopBySourceUserOrderBySimilarityScoreDesc(user);
    }
} 