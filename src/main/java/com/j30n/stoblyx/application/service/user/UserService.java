package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.application.port.in.user.UserUseCase;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.application.service.file.FileStorageService;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private static final String ERROR_USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    
    private final UserPort userPort;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(Long userId) {
        log.debug("사용자 프로필 조회: userId={}", userId);
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUser(Long userId, UserUpdateRequest request) {
        log.debug("사용자 프로필 수정: userId={}, request={}", userId, request);
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));

        // 이메일 중복 체크
        if (!user.getEmail().equals(request.email()) && 
            userPort.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.updateProfile(request.nickname(), user.getProfileImageUrl());
        user.updateEmail(request.email());
        userPort.save(user);
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("사용자 계정 삭제: userId={}", userId);
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        user.delete();
    }

    @Override
    public UserProfileResponse updateProfileImage(Long userId, MultipartFile image) {
        // 1. 사용자 조회
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. 이미지 저장 (파일 시스템 또는 클라우드 스토리지)
        String imageUrl = fileStorageService.storeFile(image);
        
        // 3. 사용자 프로필 이미지 URL 업데이트
        user.updateProfileImage(imageUrl);
        userPort.save(user);
        
        // 4. 업데이트된 프로필 반환
        return UserProfileResponse.from(user);
    }
}