package com.j30n.stoblyx.port.in;

import com.j30n.stoblyx.domain.user.User;

public interface UserUseCase {
    User registerUser(String email, String password, String name);
    User findUserById(Long id);
    User findUserByEmail(String email);
    void validateUser(String email, String password);
} 