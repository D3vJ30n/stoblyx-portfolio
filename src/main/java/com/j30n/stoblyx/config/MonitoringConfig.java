package com.j30n.stoblyx.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 모니터링 설정 클래스
 */
@Configuration
public class MonitoringConfig {

    private final Map<String, Timer> apiTimers = new HashMap<>();
    private JvmGcMetrics gcMetrics;

    /**
     * 메트릭 레지스트리 커스터마이저 빈
     *
     * @param env 환경 설정
     * @return 메트릭 레지스트리 커스터마이저
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment env) {
        return registry -> {
            // 애플리케이션 이름과 환경 정보를 태그로 추가
            registry.config()
                .commonTags("application", env.getProperty("spring.application.name", "stoblyx"))
                .commonTags("environment", env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default");

            // JVM 메모리 메트릭 등록
            JvmMemoryMetrics memoryMetrics = new JvmMemoryMetrics();
            memoryMetrics.bindTo(registry);

            // JVM GC 메트릭 등록
            gcMetrics = new JvmGcMetrics();
            gcMetrics.bindTo(registry);

            // 프로세서 메트릭 등록
            ProcessorMetrics processorMetrics = new ProcessorMetrics();
            processorMetrics.bindTo(registry);

            // 업타임 메트릭 등록
            UptimeMetrics uptimeMetrics = new UptimeMetrics();
            uptimeMetrics.bindTo(registry);
        };
    }

    /**
     * API 요청 타이머 생성 또는 조회
     *
     * @param registry 메트릭 레지스트리
     * @param apiName  API 이름
     * @return 타이머
     */
    public Timer getApiTimer(MeterRegistry registry, String apiName) {
        return apiTimers.computeIfAbsent(apiName, name -> Timer.builder("api.request.duration")
            .tag("api", name)
            .description("API 요청 처리 시간")
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .register(registry));
    }

    /**
     * API 요청 타이밍 측정
     *
     * @param registry   메트릭 레지스트리
     * @param apiName    API 이름
     * @param durationMs 소요 시간 (밀리초)
     */
    public void recordApiTiming(MeterRegistry registry, String apiName, long durationMs) {
        Timer timer = getApiTimer(registry, apiName);
        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 리소스 정리
     */
    @PreDestroy
    public void close() {
        if (gcMetrics != null) {
            gcMetrics.close();
        }
    }
} 