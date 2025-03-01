package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Auth;
import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    
    /**
     * 사용자로 인증 정보를 찾습니다.
     */
    Optional<Auth> findByUser(User user);
    
    /**
     * 리프레시 토큰으로 인증 정보를 찾습니다.
     */
    Optional<Auth> findByRefreshToken(String refreshToken);
    
    /**
     * 사용자 ID로 인증 정보를 찾습니다.
     */
    Optional<Auth> findByUserId(Long userId);
    
    /**
     * 사용자 ID로 인증 정보를 삭제합니다.
     */
    void deleteByUserId(Long userId);
} 