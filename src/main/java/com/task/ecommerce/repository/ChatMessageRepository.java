package com.task.ecommerce.repository;

import com.task.ecommerce.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findAllByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
