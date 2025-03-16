package com.j30n.stoblyx.application.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 파일을 저장하고 접근 가능한 URL을 반환합니다.
     *
     * @param file 저장할 파일
     * @return 저장된 파일의 URL
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("저장할 파일이 없습니다.");
        }

        try {
            // 파일 이름 생성 (중복 방지를 위해 UUID 사용)
            String originalFilename = StringUtils.cleanPath(
                Optional.ofNullable(file.getOriginalFilename()).orElse("unknown")
            );
            String fileExtension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID() + fileExtension;

            // 저장 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // 파일 저장
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("파일 저장 완료: {}", targetLocation);

            // 파일 URL 생성 및 반환
            return baseUrl + "/uploads/" + filename;

        } catch (IOException ex) {
            log.error("파일 저장 중 오류 발생", ex);
            throw new FileStorageException("파일 저장에 실패했습니다.", ex);
        }
    }

    /**
     * 파일 확장자를 추출합니다.
     */
    private String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    // 클래스 내부에 전용 예외 클래스 추가
    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message) {
            super(message);
        }

        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 