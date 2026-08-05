package com.skyl1n.portal.repository;

import com.skyl1n.portal.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByOrderByCreatedAtDesc();
}
