package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * TTS API 클라이언트
 * 텍스트를 음성으로 변환하는 기능을 담당합니다.
 */
@Slf4j
@Component
public class TTSClient {
    private final Path audioPath;
    private final String pythonScriptPath;

    public TTSClient() {
        this.audioPath = Paths.get("audio");
        this.audioPath.toFile().mkdirs();
        
        // Python 스크립트 경로 설정 (프로젝트 루트 디렉토리 기준)
        this.pythonScriptPath = "test_stt.py";
    }

    /**
     * 텍스트를 음성으로 변환합니다.
     *
     * @param text 음성으로 변환할 텍스트
     * @return 생성된 음성 파일의 URL
     */
    public String generateSpeech(String text) {
        log.info("TTS 생성 요청: text={}", text);
        
        try {
            // UUID로 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + ".mp3";
            File outputFile = audioPath.resolve(fileName).toFile();
            
            // ProcessBuilder를 사용하여 Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder(
                "python", 
                pythonScriptPath, 
                text, 
                outputFile.getAbsolutePath()
            );
            
            // 현재 작업 디렉토리 설정
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);
            
            log.info("Python TTS 스크립트 실행: {}", pb.command());
            
            Process process = pb.start();
            
            // 프로세스 출력 로깅
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("Python TTS 출력: {}", line);
                }
            }
            
            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("TTS 생성 성공: {}", outputFile.getAbsolutePath());
                return outputFile.toURI().toString();
            } else {
                log.error("TTS 생성 실패: exitCode={}", exitCode);
                throw new TTSException("TTS 생성 실패: 종료 코드 " + exitCode);
            }
        } catch (InterruptedException e) {
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
            log.error("TTS 생성 중 중단됨", e);
            throw new TTSException("TTS 생성 중 중단됨", e);
        } catch (Exception e) {
            log.error("음성 파일 생성 실패", e);
            throw new TTSException("TTS 처리 중 오류 발생", e);
        }
    }

    /**
     * TTS API 관련 예외 클래스
     */
    public static class TTSException extends RuntimeException {
        public TTSException(String message) {
            super(message);
        }
        
        public TTSException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
