package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(Long userId) {
        log.debug("사용자 프로필 조회: userId={}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUser(Long userId, UserUpdateRequest request) {
        log.debug("사용자 프로필 수정: userId={}, request={}", userId, request);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이메일 중복 체크
        if (!user.getEmail().equals(request.email()) && 
            userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.updateProfile(request.nickname(), request.email());
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("사용자 계정 삭제: userId={}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.delete();
    }
} 