package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.domain.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TestConfiguration
@Profile("test")
@EnableTransactionManagement
public class TestDataConfig {

    @Bean
    public boolean initializeTestData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // 기존 데이터 삭제
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        User testUser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();

        userRepository.save(testUser);
        
        return true;
    }
} 