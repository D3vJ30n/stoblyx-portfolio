package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BGM 서비스 클라이언트
 * 텍스트 감정 분석을 통해 적절한 BGM을 선택합니다.
 */
@Slf4j
@Component
public class BGMClient {
    private static final String BGM_PATH = "static/bgm/";
    private static final String DEFAULT_BGM = "neutral.mp3";
    private static final String EMOTION_HAPPY = "happy";
    private static final String EMOTION_SAD = "sad";
    private static final String EMOTION_CALM = "calm";
    private static final String EMOTION_NEUTRAL = DEFAULT_BGM.replace(".mp3", "");
    private final Map<String, String> emotionBgmMap;
    // 감정별 키워드 매핑
    private final Map<String, List<String>> emotionKeywords;

    public BGMClient() {
        // BGM 파일 매핑 초기화
        emotionBgmMap = new HashMap<>();
        emotionBgmMap.put(EMOTION_HAPPY, BGM_PATH + "happy.mp3");
        emotionBgmMap.put(EMOTION_SAD, BGM_PATH + "sad.mp3");
        emotionBgmMap.put(EMOTION_CALM, BGM_PATH + "calm.mp3");
        emotionBgmMap.put(EMOTION_NEUTRAL, BGM_PATH + DEFAULT_BGM);

        // 감정 키워드 초기화
        emotionKeywords = new HashMap<>();
        emotionKeywords.put(EMOTION_HAPPY, Arrays.asList(
            "기쁨", "행복", "즐거움", "웃음", "희망", "성공", "축하", "승리", "사랑", "감사",
            "happy", "joy", "pleasure", "love", "success", "wonderful", "excited"
        ));
        emotionKeywords.put(EMOTION_SAD, Arrays.asList(
            "슬픔", "우울", "상실", "이별", "고통", "아픔", "눈물", "실패", "좌절", "죽음", "후회",
            "sad", "sorrow", "pain", "grief", "depression", "regret", "tears"
        ));
        emotionKeywords.put(EMOTION_CALM, Arrays.asList(
            "평화", "평온", "침착", "고요", "안정", "명상", "자연", "휴식", "고요함", "깊이",
            "calm", "peace", "quiet", "tranquil", "serene", "meditation", "still"
        ));

        // 파일 존재 여부 확인
        checkBgmFiles();
    }

    /**
     * BGM 파일이 실제로 존재하는지 확인합니다.
     */
    private void checkBgmFiles() {
        for (String bgmPath : emotionBgmMap.values()) {
            try {
                new ClassPathResource(bgmPath).getInputStream().close();
                log.info("BGM 파일 확인: {}", bgmPath);
            } catch (Exception e) {
                log.warn("BGM 파일을 찾을 수 없습니다: {}", bgmPath);
            }
        }
    }

    /**
     * 텍스트 분석을 통해 적절한 BGM을 선택합니다.
     *
     * @param text 분석할 텍스트 (책 내용 또는 검색어)
     * @return 선택된 BGM 파일의 경로
     */
    public String selectBGMByText(String text) {
        if (text == null || text.trim().isEmpty()) {
            log.info("텍스트가 비어있어 기본 BGM을 선택합니다.");
            return emotionBgmMap.get(EMOTION_NEUTRAL);
        }

        log.info("텍스트 감정 분석 시작: {}", text.substring(0, Math.min(50, text.length())));

        // 간단한 키워드 기반 감정 분석
        Map<String, Integer> emotionScores = analyzeEmotionByKeywords(text);

        // 가장 높은 점수의 감정 선택
        String dominantEmotion = findDominantEmotion(emotionScores);
        log.info("감정 분석 결과: {} (점수: {})", dominantEmotion, emotionScores.get(dominantEmotion));

        // 해당 감정에 맞는 BGM 선택
        String selectedBgm = emotionBgmMap.getOrDefault(dominantEmotion, emotionBgmMap.get(EMOTION_NEUTRAL));
        log.info("선택된 BGM: {}", selectedBgm);

        return selectedBgm;
    }

    /**
     * 간단한 키워드 기반 감정 분석을 수행합니다.
     */
    private Map<String, Integer> analyzeEmotionByKeywords(String text) {
        String lowerText = text.toLowerCase();
        Map<String, Integer> scores = new HashMap<>();

        // 각 감정별 점수 초기화
        scores.put(EMOTION_HAPPY, 0);
        scores.put(EMOTION_SAD, 0);
        scores.put(EMOTION_CALM, 0);
        scores.put(EMOTION_NEUTRAL, 1); // 기본값 1

        // 감정별 키워드 매칭
        for (Map.Entry<String, List<String>> entry : emotionKeywords.entrySet()) {
            String emotion = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                // 단어 경계를 고려한 패턴 매칭
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword.toLowerCase()) + "\\b");
                java.util.regex.Matcher matcher = pattern.matcher(lowerText);

                while (matcher.find()) {
                    scores.put(emotion, scores.get(emotion) + 1);
                }
            }
        }

        return scores;
    }

    /**
     * 가장 높은 점수를 가진 감정을 찾습니다.
     */
    private String findDominantEmotion(Map<String, Integer> emotionScores) {
        return emotionScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(EMOTION_NEUTRAL);
    }

    /**
     * 기본 BGM을 선택합니다. (외부 호출용)
     */
    public String selectBGM() {
        log.info("기본 BGM 선택");
        return emotionBgmMap.get(EMOTION_NEUTRAL);
    }

    /**
     * BGM API 관련 예외 클래스
     */
    public static class BGMException extends RuntimeException {
        public BGMException(String message) {
            super(message);
        }
        
        public BGMException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 무작위 BGM URL을 반환합니다.
     * @return BGM 파일의 URL
     */
    public String getRandomBGM() {
        log.info("무작위 BGM 선택");
        
        // 모든 BGM 파일 경로 리스트
        List<String> allBgmPaths = new ArrayList<>(emotionBgmMap.values());
        
        // 랜덤 선택
        int randomIndex = (int) (Math.random() * allBgmPaths.size());
        String selectedBgm = allBgmPaths.get(randomIndex);
        
        log.info("무작위 선택된 BGM: {}", selectedBgm);
        return selectedBgm;
    }
    
    /**
     * 지정된 분위기에 맞는 BGM URL을 반환합니다.
     * @param mood 분위기 키워드
     * @return BGM 파일의 URL
     */
    public String selectBGMByMood(String mood) {
        log.info("분위기 기반 BGM 선택: {}", mood);
        
        if (mood == null || mood.trim().isEmpty()) {
            log.info("분위기 키워드가 비어있어 기본 BGM을 선택합니다.");
            return emotionBgmMap.get(EMOTION_NEUTRAL);
        }
        
        // 소문자로 변환
        String lowerMood = mood.toLowerCase().trim();
        
        // 감정 키워드 매칭 확인
        for (Map.Entry<String, List<String>> entry : emotionKeywords.entrySet()) {
            String emotion = entry.getKey();
            List<String> keywords = entry.getValue();
            
            // 키워드 중 하나라도 포함되면 해당 감정의 BGM 반환
            if (keywords.stream().anyMatch(lowerMood::contains) || lowerMood.contains(emotion)) {
                String selectedBgm = emotionBgmMap.get(emotion);
                log.info("분위기 '{}' -> 감정 '{}' -> BGM '{}'", mood, emotion, selectedBgm);
                return selectedBgm;
            }
        }
        
        // 매칭되는 키워드가 없으면 기본 BGM 반환
        log.info("분위기 '{}' 에 매칭되는 BGM이 없어 기본 BGM을 선택합니다.", mood);
        return emotionBgmMap.get(EMOTION_NEUTRAL);
    }
}
