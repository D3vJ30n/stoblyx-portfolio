package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * BGM API 클라이언트
 * 상황에 맞는 BGM을 선택하는 기능을 담당합니다.
 */
@Slf4j
@Component
public class BGMClient {
    private static final List<String> DEFAULT_BGM_FILES = Arrays.asList(
        "calm.mp3",
        "happy.mp3",
        "sad.mp3",
        "neutral.mp3"
    );

    private final ResourceLoader resourceLoader;
    private final Path bgmPath;
    private final Random random;

    @Value("${bgm.directory:bgm}")
    private String bgmDirectory;

    public BGMClient(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.bgmPath = Paths.get(bgmDirectory);
        this.random = new Random();
        this.bgmPath.toFile().mkdirs();
        initializeDefaultBGMs();
    }

    /**
     * 상황에 맞는 BGM을 선택합니다.
     *
     * @return 선택된 BGM의 URL
     */
    public String selectBGM() {
        try {
            List<Path> bgmFiles = Files.list(bgmPath)
                .filter(path -> path.toString().endsWith(".mp3"))
                .toList();

            if (bgmFiles.isEmpty()) {
                log.warn("BGM 파일이 없습니다. 기본 BGM을 사용합니다.");
                return DEFAULT_BGM_FILES.get(random.nextInt(DEFAULT_BGM_FILES.size()));
            }

            Path selectedBGM = bgmFiles.get(random.nextInt(bgmFiles.size()));
            return selectedBGM.toUri().toString();
        } catch (IOException e) {
            log.error("BGM 파일 목록을 가져오는데 실패했습니다.", e);
            return DEFAULT_BGM_FILES.get(random.nextInt(DEFAULT_BGM_FILES.size()));
        }
    }

    private void initializeDefaultBGMs() {
        try {
            if (Files.list(bgmPath).findAny().isEmpty()) {
                log.info("기본 BGM 파일을 생성합니다.");
                for (String bgmFile : DEFAULT_BGM_FILES) {
                    Path bgmPath = this.bgmPath.resolve(bgmFile);
                    if (!Files.exists(bgmPath)) {
                        Files.createFile(bgmPath);
                    }
                }
            }
        } catch (IOException e) {
            log.error("기본 BGM 파일 생성에 실패했습니다.", e);
        }
    }
}
