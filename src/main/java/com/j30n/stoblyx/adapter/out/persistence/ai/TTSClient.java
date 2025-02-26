package com.j30n.stoblyx.adapter.out.persistence.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
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
    private static final float SAMPLE_RATE = 44100;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = true;

    public TTSClient() {
        this.audioPath = Paths.get("audio");
        this.audioPath.toFile().mkdirs();
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
            // 간단한 사인파 생성
            byte[] audioData = generateSineWaveAudio();
            
            // WAV 파일로 저장
            String fileName = UUID.randomUUID().toString() + ".wav";
            File outputFile = audioPath.resolve(fileName).toFile();
            
            AudioFormat format = new AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE_IN_BITS,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
            );
            
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais = new AudioInputStream(
                bais,
                format,
                audioData.length / format.getFrameSize()
            );
            
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            
            return outputFile.toURI().toString();
        } catch (Exception e) {
            log.error("음성 파일 생성 실패", e);
            return null;
        }
    }

    private byte[] generateSineWaveAudio() {
        // 1초 길이의 440Hz 사인파 생성
        int numSamples = (int) SAMPLE_RATE;
        byte[] buffer = new byte[2 * numSamples];
        double frequency = 440.0; // A4 음
        
        for (int i = 0; i < numSamples; i++) {
            double time = i / SAMPLE_RATE;
            double angle = 2.0 * Math.PI * frequency * time;
            short sample = (short) (Short.MAX_VALUE * Math.sin(angle));
            
            buffer[2*i] = (byte) (sample & 0xFF);
            buffer[2*i+1] = (byte) ((sample >> 8) & 0xFF);
        }
        
        return buffer;
    }
}
