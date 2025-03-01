package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.application.port.out.user.UserInterestPort;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserInterest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestService implements UserInterestUseCase {

    private final UserPort userPort;
    private final UserInterestPort userInterestPort;

    @Override
    @Transactional(readOnly = true)
    public UserInterestResponse getUserInterest(Long userId) {
        log.debug("사용자 관심사 조회: userId={}", userId);
        
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestPort.findByUserId(userId)
            .orElse(UserInterest.createEmpty(user));

        return UserInterestResponse.from(userInterest);
    }

    @Override
    @Transactional
    public UserInterestResponse updateUserInterest(Long userId, UserInterestRequest request) {
        log.debug("사용자 관심사 수정: userId={}, request={}", userId, request);
        
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestPort.findByUserId(userId)
            .orElse(UserInterest.createEmpty(user));

        userInterest.updateInterests(request.genres(), request.authors(), request.keywords());
        userInterestPort.save(userInterest);

        return UserInterestResponse.from(userInterest);
    }
}