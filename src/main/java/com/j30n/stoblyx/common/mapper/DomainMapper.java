package com.j30n.stoblyx.common.mapper;

import java.util.List;
import java.util.Optional;

/**
 * 도메인 엔티티와 JPA 엔티티 간의 변환을 위한 공통 매퍼 인터페이스
 *
 * @param <D> 도메인 엔티티 타입
 * @param <E> JPA 엔티티 타입
 */
public interface DomainMapper<D, E> {
    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 엔티티
     * @return JPA 엔티티 (Optional)
     */
    Optional<E> toEntity(D domain);

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 엔티티 (Optional)
     */
    Optional<D> toDomain(E entity);

    /**
     * 도메인 엔티티 목록을 JPA 엔티티 목록으로 변환합니다.
     *
     * @param domains 도메인 엔티티 목록
     * @return JPA 엔티티 목록
     */
    default List<E> toEntityList(List<D> domains) {
        return domains.stream()
            .map(this::toEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    /**
     * JPA 엔티티 목록을 도메인 엔티티 목록으로 변환합니다.
     *
     * @param entities JPA 엔티티 목록
     * @return 도메인 엔티티 목록
     */
    default List<D> toDomainList(List<E> entities) {
        return entities.stream()
            .map(this::toDomain)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
} 