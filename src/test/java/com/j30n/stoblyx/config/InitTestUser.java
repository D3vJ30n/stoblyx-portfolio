package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("test")
public class InitTestUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initTestData() {
        return args -> {
            // 테스트 사용자 생성
            createTestUser();
        };
    }

    @Transactional
    public void createTestUser() {
        // 테스트 사용자가 이미 존재하는지 확인
        if (userRepository.findByUsername("testuser").isPresent()) {
            log.info("테스트 사용자가 이미 존재합니다.");
            return;
        }

        // 테스트 사용자 생성 및 저장
        User testUser = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password(passwordEncoder.encode("Password123!"))
                .nickname("테스트 사용자")
                .role(UserRole.USER)
                .build();

        userRepository.save(testUser);
        log.info("테스트 사용자가 생성되었습니다: {}", testUser.getUsername());
    }
} 