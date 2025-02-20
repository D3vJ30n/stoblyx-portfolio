package com.j30n.stoblyx.domain.port.out.user;

import com.j30n.stoblyx.domain.model.user.User;

import java.util.Optional;

public interface UserPort {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
} 