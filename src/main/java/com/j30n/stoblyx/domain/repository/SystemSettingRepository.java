package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.SettingCategory;
import com.j30n.stoblyx.domain.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 시스템 설정 엔티티에 대한 데이터 액세스 인터페이스
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    /**
     * 설정 키로 시스템 설정을 조회합니다.
     *
     * @param key 설정 키
     * @return 시스템 설정 Optional 객체
     */
    Optional<SystemSetting> findByKey(String key);

    /**
     * 카테고리별로 시스템 설정 목록을 조회합니다.
     *
     * @param category 설정 카테고리
     * @return 해당 카테고리의 시스템 설정 목록
     */
    List<SystemSetting> findByCategory(SettingCategory category);

    /**
     * 키 패턴으로 시스템 설정 목록을 조회합니다.
     *
     * @param keyPattern 키 패턴 (LIKE 검색)
     * @return 키 패턴과 일치하는 시스템 설정 목록
     */
    @Query("SELECT s FROM SystemSetting s WHERE s.key LIKE %:keyPattern%")
    List<SystemSetting> findByKeyPattern(@Param("keyPattern") String keyPattern);

    /**
     * 암호화된 설정 목록을 조회합니다.
     *
     * @return 암호화된 시스템 설정 목록
     */
    List<SystemSetting> findByEncryptedTrue();

    /**
     * 시스템 관리 설정 목록을 조회합니다.
     *
     * @return 시스템 관리 설정 목록
     */
    List<SystemSetting> findBySystemManagedTrue();

    /**
     * 키가 존재하는지 확인합니다.
     *
     * @param key 설정 키
     * @return 키가 존재하면 true, 아니면 false
     */
    boolean existsByKey(String key);
} 