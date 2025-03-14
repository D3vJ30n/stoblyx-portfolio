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
            createAllTestUsers();
        };
    }

    @Transactional
    public void createAllTestUsers() {
        // 일반 테스트 사용자 생성
        if (!userRepository.findByUsername("testuser").isPresent()) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("testuser@example.com")
                    .password(passwordEncoder.encode("Password123!"))
                    .nickname("테스트 사용자")
                    .role(UserRole.USER)
                    .build();

            userRepository.save(testUser);
            log.info("테스트 사용자가 생성되었습니다: {}", testUser.getUsername());
        } else {
            log.info("테스트 사용자가 이미 존재합니다.");
        }
        
        // 관리자 권한을 가진 테스트 사용자 생성
        if (!userRepository.findByUsername("testadmin").isPresent()) {
            User testAdmin = User.builder()
                    .username("testadmin")
                    .email("testadmin@example.com")
                    .password(passwordEncoder.encode("Password123!"))
                    .nickname("테스트 관리자")
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(testAdmin);
            log.info("테스트 관리자가 생성되었습니다: {}", testAdmin.getUsername());
        } else {
            log.info("테스트 관리자가 이미 존재합니다.");
        }
        
        // K6 테스트용 사용자 생성
        if (!userRepository.findByUsername("k6testuser").isPresent()) {
            User k6TestUser = User.builder()
                    .username("k6testuser")
                    .email("k6test@example.com")
                    .password(passwordEncoder.encode("Test1234!"))
                    .nickname("K6 테스트 사용자")
                    .role(UserRole.USER)
                    .build();

            userRepository.save(k6TestUser);
            log.info("K6 테스트 사용자가 생성되었습니다: {}", k6TestUser.getUsername());
        } else {
            log.info("K6 테스트 사용자가 이미 존재합니다.");
        }
    }
} 