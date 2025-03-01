package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    Optional<UserInterest> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
} 