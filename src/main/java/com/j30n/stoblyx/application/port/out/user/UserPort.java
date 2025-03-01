package com.j30n.stoblyx.application.port.out.user;

import com.j30n.stoblyx.domain.model.User;
import java.util.Optional;

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
}
