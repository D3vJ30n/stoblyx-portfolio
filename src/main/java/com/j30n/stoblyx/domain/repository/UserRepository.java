package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    // 특정 기간 내 생성된 사용자 조회
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 특정 기간 내 일별 로그인 통계 조회
    @Query("SELECT FUNCTION('DATE', u.lastLoginAt) as date, COUNT(u) as count FROM User u " +
           "WHERE u.lastLoginAt BETWEEN :start AND :end GROUP BY FUNCTION('DATE', u.lastLoginAt)")
    List<Object[]> countLoginsByDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 특정 사용자의 특정 기간 내 로그인 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.id = :userId AND u.lastLoginAt BETWEEN :start AND :end")
    long countLoginsByUserIdAndDateBetween(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
} 