package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * 특정 날짜 이후에 생성된 사용자 수를 조회합니다.
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    /**
     * 특정 날짜 이후에 로그인한 사용자 수를 조회합니다.
     */
    long countByLastLoginAtAfter(LocalDateTime dateTime);
} 