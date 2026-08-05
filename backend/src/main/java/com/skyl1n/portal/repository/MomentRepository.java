package com.skyl1n.portal.repository;

import com.skyl1n.portal.entity.Moment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    List<Moment> findAllByOrderByCreatedAtDesc();
}
