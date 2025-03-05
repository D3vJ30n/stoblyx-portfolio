package com.j30n.stoblyx.application.service.recommendation;

import com.j30n.stoblyx.adapter.in.web.dto.recommendation.PopularTermResponse;
import com.j30n.stoblyx.adapter.in.web.dto.recommendation.RecommendationRequest;
import com.j30n.stoblyx.adapter.in.web.dto.recommendation.RecommendationResponse;
import com.j30n.stoblyx.application.port.in.recommendation.RecommendationUseCase;
import com.j30n.stoblyx.application.port.out.recommendation.RecommendationPort;
import com.j30n.stoblyx.domain.model.SearchTermProfile;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 검색어 기반 사용자 추천 시스템 서비스
 */
@Service
@RequiredArgsConstructor
public class RecommendationService implements RecommendationUseCase {

    private final RecommendationPort recommendationPort;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userRecommendations", key = "#userId")
    public Page<RecommendationResponse> getUserRecommendations(Long userId, Pageable pageable) {
        return recommendationPort.getUserSimilarities(userId, pageable)
                .map(RecommendationResponse::fromEntity);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "userRecommendations", allEntries = true)
    public Integer runCollaborativeFiltering(RecommendationRequest request) {
        // 모든 사용자 목록 조회
        List<User> allUsers = recommendationPort.getAllUsers();
        
        // 사용자-검색어 행렬 구성
        Map<Long, Map<String, Double>> userTermMatrix = buildUserTermMatrix(allUsers);
        
        // 사용자 간 유사도 계산
        Map<Long, Map<Long, Double>> similarityMatrix = calculateSimilarityMatrix(userTermMatrix);
        
        // 추천 결과 저장
        int updatedCount = 0;
        for (Long userId : similarityMatrix.keySet()) {
            Map<Long, Double> similarities = similarityMatrix.get(userId);
            
            // 유사도 임계값 이상인 사용자만 추천
            double threshold = request.similarityThreshold();
            
            for (Map.Entry<Long, Double> entry : similarities.entrySet()) {
                if (entry.getValue() >= threshold && !Objects.equals(userId, entry.getKey())) {
                    recommendationPort.saveUserSimilarity(userId, entry.getKey(), entry.getValue());
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "userRecommendations", key = "#userId")
    public Integer updateUserRecommendations(Long userId) {
        // 사용자의 검색어 목록 조회
        List<SearchTermProfile> userTerms = recommendationPort.getUserSearchTerms(userId);
        
        if (userTerms.isEmpty()) {
            return 0;
        }
        
        // 다른 모든 사용자 조회
        List<User> allUsers = recommendationPort.getAllUsers();
        
        // 사용자 검색어 벡터 생성
        Map<String, Double> userVector = userTerms.stream()
                .collect(Collectors.toMap(
                        SearchTermProfile::getSearchTerm,
                        term -> term.getSearchFrequency() * term.getTermWeight()
                ));
        
        int updatedCount = 0;
        
        // 각 사용자와의 유사도 계산 및 추천 저장
        for (User otherUser : allUsers) {
            if (Objects.equals(otherUser.getId(), userId)) {
                continue;
            }
            
            List<SearchTermProfile> otherUserTerms = recommendationPort.getUserSearchTerms(otherUser.getId());
            
            if (otherUserTerms.isEmpty()) {
                continue;
            }
            
            Map<String, Double> otherUserVector = otherUserTerms.stream()
                    .collect(Collectors.toMap(
                            SearchTermProfile::getSearchTerm,
                            term -> term.getSearchFrequency() * term.getTermWeight()
                    ));
            
            double similarity = calculateCosineSimilarity(userVector, otherUserVector);
            
            if (similarity > 0.3) {  // 유사도 임계값
                recommendationPort.saveUserSimilarity(userId, otherUser.getId(), similarity);
                updatedCount++;
            }
        }
        
        return updatedCount;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "popularTerms")
    public Page<PopularTermResponse> getPopularTerms(Pageable pageable) {
        return recommendationPort.getPopularTerms(pageable)
                .map(PopularTermResponse::fromEntity);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "popularTerms", allEntries = true)
    public Integer updatePopularTerms() {
        // 최근 24시간 검색 기록 조회
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        Map<String, Integer> recentSearchCounts = recommendationPort.getRecentSearchCounts(since);
        
        int updatedCount = 0;
        
        // 각 검색어의 인기도 점수 계산 및 저장
        for (Map.Entry<String, Integer> entry : recentSearchCounts.entrySet()) {
            String term = entry.getKey();
            Integer count = entry.getValue();
            
            // 인기도 점수 계산 (간단한 예: 검색 횟수 / 시간 가중치)
            double popularityScore = calculatePopularityScore(count, since);
            
            recommendationPort.savePopularTerm(term, count, popularityScore);
            updatedCount++;
        }
        
        return updatedCount;
    }
    
    /**
     * 인기 검색어 정기 업데이트 (1시간마다 실행)
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void scheduledPopularTermsUpdate() {
        updatePopularTerms();
    }
    
    /**
     * 사용자 추천 정기 업데이트 (매일 새벽 3시에 실행)
     */
    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void scheduledRecommendationsUpdate() {
        RecommendationRequest request = new RecommendationRequest(0.3, 10, false);
        runCollaborativeFiltering(request);
    }
    
    /**
     * 사용자-검색어 행렬 구성
     */
    private Map<Long, Map<String, Double>> buildUserTermMatrix(List<User> users) {
        Map<Long, Map<String, Double>> matrix = new HashMap<>();
        
        for (User user : users) {
            List<SearchTermProfile> userTerms = recommendationPort.getUserSearchTerms(user.getId());
            
            Map<String, Double> termWeights = userTerms.stream()
                    .collect(Collectors.toMap(
                            SearchTermProfile::getSearchTerm,
                            term -> term.getSearchFrequency() * term.getTermWeight()
                    ));
            
            matrix.put(user.getId(), termWeights);
        }
        
        return matrix;
    }
    
    /**
     * 사용자 간 유사도 행렬 계산
     */
    private Map<Long, Map<Long, Double>> calculateSimilarityMatrix(Map<Long, Map<String, Double>> userTermMatrix) {
        Map<Long, Map<Long, Double>> similarityMatrix = new HashMap<>();
        
        List<Long> userIds = new ArrayList<>(userTermMatrix.keySet());
        
        for (int i = 0; i < userIds.size(); i++) {
            Long userId1 = userIds.get(i);
            Map<String, Double> vector1 = userTermMatrix.get(userId1);
            
            Map<Long, Double> similarities = new HashMap<>();
            similarityMatrix.put(userId1, similarities);
            
            for (int j = 0; j < userIds.size(); j++) {
                Long userId2 = userIds.get(j);
                Map<String, Double> vector2 = userTermMatrix.get(userId2);
                
                double similarity = calculateCosineSimilarity(vector1, vector2);
                similarities.put(userId2, similarity);
            }
        }
        
        return similarityMatrix;
    }
    
    /**
     * 코사인 유사도 계산
     */
    private double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        if (vector1.isEmpty() || vector2.isEmpty()) {
            return 0.0;
        }
        
        // 두 벡터의 내적 계산
        double dotProduct = 0.0;
        for (String term : vector1.keySet()) {
            if (vector2.containsKey(term)) {
                dotProduct += vector1.get(term) * vector2.get(term);
            }
        }
        
        // 벡터 크기 계산
        double magnitude1 = Math.sqrt(vector1.values().stream().mapToDouble(v -> v * v).sum());
        double magnitude2 = Math.sqrt(vector2.values().stream().mapToDouble(v -> v * v).sum());
        
        // 코사인 유사도 계산
        if (magnitude1 > 0 && magnitude2 > 0) {
            return dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
    }
    
    /**
     * 인기도 점수 계산
     */
    private double calculatePopularityScore(int searchCount, LocalDateTime since) {
        // 현재 시간과의 차이 계산 (시간 단위)
        long hoursDiff = java.time.Duration.between(since, LocalDateTime.now()).toHours();
        
        // 시간에 따른 가중치 계산 (최근일수록 높은 가중치)
        double timeWeight = 1.0 + (24.0 - hoursDiff) / 24.0;
        
        // 인기도 점수 = 검색 횟수 * 시간 가중치
        return searchCount * timeWeight;
    }
} 