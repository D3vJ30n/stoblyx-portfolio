package com.j30n.stoblyx.application.port.out.user;

import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관련 포트 아웃 인터페이스
 */
public interface UserPort {
    /**
     * ID로 사용자를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(Long userId);

    /**
     * 이메일로 사용자 존재 여부를 확인합니다.
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 사용자를 저장합니다.
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * 모든 사용자를 페이징하여 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 사용자 페이지
     */
    Page<User> findAll(Pageable pageable);

    /**
     * 사용자 정보 저장
     *
     * @param user 사용자 정보
     * @return 저장된 사용자 정보
     */
    User saveUser(User user);

    /**
     * 사용자명으로 사용자 정보 조회
     *
     * @param username 사용자명
     * @return 사용자 정보
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명으로 사용자 정보 조회
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    Optional<User> findByEmail(String email);

    /**
     * 모든 사용자 정보 조회
     *
     * @return 모든 사용자 정보 목록
     */
    List<User> findAll();

    /**
     * 활성 상태인 사용자 수 조회
     *
     * @return 활성 상태인 사용자 수
     */
    Long countActiveUsers();

    /**
     * 특정 역할을 가진 사용자 목록 조회
     *
     * @param role 역할
     * @return 해당 역할을 가진 사용자 목록
     */
    List<User> findByRole(String role);

    /**
     * 사용자 정보 삭제
     *
     * @param userId 사용자 ID
     */
    void deleteUser(Long userId);
}
