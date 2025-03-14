package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 사용자명으로 사용자 정보 조회
     *
     * @param username 사용자명
     * @return 사용자 정보
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일로 사용자 정보 조회
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명 존재 여부 확인
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 활성 상태인 사용자 수 조회
     *
     * @return 활성 상태인 사용자 수
     */
    Long countByIsDeletedFalse();
    
    /**
     * 특정 역할을 가진 사용자 목록 조회
     *
     * @param role 역할
     * @return 해당 역할을 가진 사용자 목록
     */
    List<User> findByRole(String role);

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