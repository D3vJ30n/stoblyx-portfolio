package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserInterest;
import com.j30n.stoblyx.domain.repository.UserInterestRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestServiceImpl implements UserInterestService {

    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;

    @Override
    @Transactional(readOnly = true)
    public UserInterestResponse getUserInterest(Long userId) {
        log.debug("사용자 관심사 조회: userId={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자 관심사가 등록되지 않았습니다."));

        return UserInterestResponse.from(userInterest);
    }

    @Override
    @Transactional
    public UserInterestResponse updateUserInterest(Long userId, UserInterestRequest request) {
        log.debug("사용자 관심사 수정: userId={}, request={}", userId, request);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserInterest newUserInterest = UserInterest.builder()
                    .user(user)
                    .genres(request.genres())
                    .topics(request.topics())
                    .bio(request.bio())
                    .build();
                return userInterestRepository.save(newUserInterest);
            });

        userInterest.update(request.genres(), request.topics(), request.bio());
        return UserInterestResponse.from(userInterest);
    }
} 