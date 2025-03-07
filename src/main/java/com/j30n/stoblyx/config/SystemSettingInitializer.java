package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.enums.SettingCategory;
import com.j30n.stoblyx.domain.model.SystemSetting;
import com.j30n.stoblyx.domain.repository.SystemSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * 시스템 설정 초기화를 위한 설정 클래스
 * 애플리케이션 시작 시 기본 시스템 설정을 데이터베이스에 로드합니다.
 */
@Configuration
public class SystemSettingInitializer {

    private static final String DIGITS_ONLY_PATTERN = "^\\d+$";
    
    private final Environment env;
    
    public SystemSettingInitializer(Environment env) {
        this.env = env;
    }

    /**
     * 시스템 설정 초기화를 위한 CommandLineRunner 빈
     *
     * @param repository 시스템 설정 리포지토리
     * @return CommandLineRunner 인스턴스
     */
    @Bean
    public CommandLineRunner initSystemSettings(SystemSettingRepository repository) {
        return args -> {
            // 이미 초기화되었는지 확인
            if (repository.count() > 0) {
                return;
            }

            // 기본 시스템 설정 목록 생성
            List<SystemSetting> defaultSettings = Arrays.asList(
                    // API 키 설정
                    SystemSetting.builder()
                            .key("api.key.pexels")
                            .value(env.getProperty("pexels.api.key", ""))
                            .description("Pexels API 키")
                            .category(SettingCategory.API_KEY)
                            .encrypted(true)
                            .systemManaged(true)
                            .build(),

                    // 리소스 경로 설정
                    SystemSetting.builder()
                            .key("resource.path.media")
                            .value("./media")
                            .description("미디어 파일 저장 경로")
                            .category(SettingCategory.RESOURCE_PATH)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("./media")
                            .build(),

                    SystemSetting.builder()
                            .key("resource.path.temp")
                            .value("./temp")
                            .description("임시 파일 저장 경로")
                            .category(SettingCategory.RESOURCE_PATH)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("./temp")
                            .build(),

                    // 캐시 설정
                    SystemSetting.builder()
                            .key("cache.ttl.popular_quotes")
                            .value("3600")
                            .description("인기 문구 캐시 TTL (초)")
                            .category(SettingCategory.CACHE)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("3600")
                            .build(),

                    SystemSetting.builder()
                            .key("cache.ttl.trending_content")
                            .value("1800")
                            .description("트렌딩 콘텐츠 캐시 TTL (초)")
                            .category(SettingCategory.CACHE)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("1800")
                            .build(),

                    // 랭킹 시스템 설정
                    SystemSetting.builder()
                            .key("ranking.param.alpha")
                            .value("0.2")
                            .description("EWMA 알고리즘 알파값 (최근 활동 가중치)")
                            .category(SettingCategory.RANKING)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("0.2")
                            .validationPattern("^0(\\.[0-9]{1,2})?$")
                            .build(),

                    SystemSetting.builder()
                            .key("ranking.param.decay_factor")
                            .value("0.05")
                            .description("비활동 사용자 점수 감소 계수 (7일마다)")
                            .category(SettingCategory.RANKING)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("0.05")
                            .validationPattern("^0(\\.[0-9]{1,2})?$")
                            .build(),

                    // 게이미피케이션 설정
                    SystemSetting.builder()
                            .key("gamification.rank.threshold.bronze")
                            .value("0")
                            .description("브론즈 랭크 최소 점수")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("0")
                            .validationPattern(DIGITS_ONLY_PATTERN)
                            .build(),

                    SystemSetting.builder()
                            .key("gamification.rank.threshold.silver")
                            .value("1201")
                            .description("실버 랭크 최소 점수")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("1201")
                            .validationPattern(DIGITS_ONLY_PATTERN)
                            .build(),

                    SystemSetting.builder()
                            .key("gamification.rank.threshold.gold")
                            .value("1501")
                            .description("골드 랭크 최소 점수")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("1501")
                            .validationPattern(DIGITS_ONLY_PATTERN)
                            .build(),

                    SystemSetting.builder()
                            .key("gamification.rank.threshold.platinum")
                            .value("1801")
                            .description("플래티넘 랭크 최소 점수")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("1801")
                            .validationPattern(DIGITS_ONLY_PATTERN)
                            .build(),

                    SystemSetting.builder()
                            .key("gamification.rank.threshold.diamond")
                            .value("2101")
                            .description("다이아몬드 랭크 최소 점수")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .defaultValue("2101")
                            .validationPattern(DIGITS_ONLY_PATTERN)
                            .build(),

                    // 브론즈 랭크 혜택
                    SystemSetting.builder()
                            .key("gamification.rank.benefit.bronze")
                            .value("{\"dailyContentCreation\":3,\"premiumBgm\":false,\"advancedTemplates\":false,\"unlimitedContentCreation\":false,\"customWatermark\":false,\"contentPriorityExposure\":false}")
                            .description("브론즈 랭크 혜택")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .build(),

                    // 실버 랭크 혜택
                    SystemSetting.builder()
                            .key("gamification.rank.benefit.silver")
                            .value("{\"dailyContentCreation\":5,\"premiumBgm\":true,\"advancedTemplates\":false,\"unlimitedContentCreation\":false,\"customWatermark\":false,\"contentPriorityExposure\":false}")
                            .description("실버 랭크 혜택")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .build(),

                    // 골드 랭크 혜택
                    SystemSetting.builder()
                            .key("gamification.rank.benefit.gold")
                            .value("{\"dailyContentCreation\":10,\"premiumBgm\":true,\"advancedTemplates\":true,\"unlimitedContentCreation\":false,\"customWatermark\":false,\"contentPriorityExposure\":false}")
                            .description("골드 랭크 혜택")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .build(),

                    // 플래티넘 랭크 혜택
                    SystemSetting.builder()
                            .key("gamification.rank.benefit.platinum")
                            .value("{\"dailyContentCreation\":0,\"premiumBgm\":true,\"advancedTemplates\":true,\"unlimitedContentCreation\":true,\"customWatermark\":true,\"contentPriorityExposure\":false}")
                            .description("플래티넘 랭크 혜택")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .build(),

                    // 다이아몬드 랭크 혜택
                    SystemSetting.builder()
                            .key("gamification.rank.benefit.diamond")
                            .value("{\"dailyContentCreation\":0,\"premiumBgm\":true,\"advancedTemplates\":true,\"unlimitedContentCreation\":true,\"customWatermark\":true,\"contentPriorityExposure\":true}")
                            .description("다이아몬드 랭크 혜택")
                            .category(SettingCategory.GAMIFICATION)
                            .encrypted(false)
                            .systemManaged(true)
                            .build()
            );

            // 시스템 설정 저장
            repository.saveAll(defaultSettings);
        };
    }
} 