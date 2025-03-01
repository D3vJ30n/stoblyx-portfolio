package com.j30n.stoblyx.application.port.out.auth;

import com.j30n.stoblyx.domain.model.User;

import java.util.Optional;

/**
 * 인증 관련 외부 포트 인터페이스
 */
public interface AuthPort {
    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자 정보
     * @return 저장된 사용자 정보
     */
    User saveUser(User user);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findUserByEmail(String email);

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findUserById(Long id);

    /**
     * 리프레시 토큰 저장
     *
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     * @param expirationTime 만료 시간 (초)
     */
    void saveRefreshToken(Long userId, String refreshToken, long expirationTime);

    /**
     * 리프레시 토큰으로 사용자 ID 조회
     *
     * @param refreshToken 리프레시 토큰
     * @return 사용자 ID (Optional)
     */
    Optional<String> findUserIdByRefreshToken(String refreshToken);

    /**
     * 리프레시 토큰 삭제
     *
     * @param refreshToken 리프레시 토큰
     */
    void deleteRefreshToken(String refreshToken);

    /**
     * 액세스 토큰 블랙리스트에 추가
     *
     * @param accessToken 액세스 토큰
     * @param expirationTime 만료 시간 (초)
     */
    void addToBlacklist(String accessToken, long expirationTime);
}
