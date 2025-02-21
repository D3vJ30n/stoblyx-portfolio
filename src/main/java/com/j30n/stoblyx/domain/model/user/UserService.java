package com.j30n.stoblyx.domain.model.user;

import com.j30n.stoblyx.application.usecase.user.port.UserUseCase;
import com.j30n.stoblyx.domain.port.out.user.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserUseCase {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String email, String password, String name) {
        if (userPort.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .role(User.Role.USER)
            .build();

        return userPort.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Override
    public User findUserByEmail(String email) {
        return userPort.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Override
    public void validateUser(String email, String password) {
        User user = findUserByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
} 