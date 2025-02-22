package com.j30n.stoblyx.domain.port.out.like;

import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.like.LikeId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 좋아요 영속성을 위한 출력 포트
 * 도메인 모델과 영속성 계층 사이의 인터페이스를 정의합니다.
 */
public interface LikePort {
    /**
     * 좋아요를 저장합니다.
     *
     * @param like 저장할 좋아요
     * @return 저장된 좋아요
     */
    Like save(Like like);

    /**
     * ID로 좋아요를 조회합니다.
     *
     * @param id 좋아요 ID
     * @return 조회된 좋아요 (Optional)
     */
    Optional<Like> findById(LikeId id);

    /**
     * 인용구에 달린 좋아요 목록을 조회합니다.
     *
     * @param quote 인용구
     * @return 좋아요 목록
     */
    List<Like> findByQuote(Quote quote);

    /**
     * 사용자가 누른 좋아요 목록을 조회합니다.
     *
     * @param user 사용자
     * @return 좋아요 목록
     */
    List<Like> findByUser(User user);

    /**
     * 사용자가 인용구에 누른 좋아요를 조회합니다.
     *
     * @param quote 인용구
     * @param user 사용자
     * @return 좋아요 (Optional)
     */
    Optional<Like> findByQuoteAndUser(Quote quote, User user);

    /**
     * 인용구의 활성화된 좋아요 수를 조회합니다.
     *
     * @param quote 인용구
     * @return 좋아요 수
     */
    long countActiveByQuote(Quote quote);
} 