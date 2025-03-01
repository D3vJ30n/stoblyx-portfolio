package com.j30n.stoblyx.adapter.out.persistence.user;

import com.j30n.stoblyx.application.port.out.user.UserInterestPort;
import com.j30n.stoblyx.domain.model.UserInterest;
import com.j30n.stoblyx.domain.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserInterestPersistenceAdapter implements UserInterestPort {
    private final UserInterestRepository userInterestRepository;

    @Override
    public Optional<UserInterest> findByUserId(Long userId) {
        return userInterestRepository.findByUserId(userId);
    }

    @Override
    public UserInterest save(UserInterest userInterest) {
        return userInterestRepository.save(userInterest);
    }
}
