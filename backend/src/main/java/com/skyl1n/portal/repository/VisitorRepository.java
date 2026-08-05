package com.skyl1n.portal.repository;

import com.skyl1n.portal.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findBySessionId(String sessionId);
}
