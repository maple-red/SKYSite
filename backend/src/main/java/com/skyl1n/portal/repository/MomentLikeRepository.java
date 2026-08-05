package com.skyl1n.portal.repository;

import com.skyl1n.portal.entity.MomentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MomentLikeRepository extends JpaRepository<MomentLike, Long> {
    Optional<MomentLike> findByMomentIdAndSessionId(Long momentId, String sessionId);
    int countByMomentId(Long momentId);
}
