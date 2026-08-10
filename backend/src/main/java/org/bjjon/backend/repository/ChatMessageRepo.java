package org.bjjon.backend.repository;

import org.bjjon.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllByOrderByCreatedAtAsc();
}
