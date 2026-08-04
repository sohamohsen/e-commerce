package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Conversation;
import com.task.ecommerce.entity.enums.ConversationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByCustomerIdAndStatusIn(
            Integer customerId,
            List<ConversationStatus> statuses
    );

    List<Conversation> findAllByStatusOrderByCreatedAtAsc(
            ConversationStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c
            from Conversation c
            where c.id = :id
            """)
    Optional<Conversation> findByIdForUpdate(Long id);

    boolean existsByAdminIdAndStatus(Integer adminId, ConversationStatus conversationStatus);

    Optional<Conversation> findFirstByCustomerIdAndStatusInOrderByUpdatedAtDesc(
            Integer customerId,
            List<ConversationStatus> statuses
    );

    Optional<Conversation> findFirstByAdminIdAndStatusOrderByUpdatedAtDesc(
            Integer adminId,
            ConversationStatus status
    );

}
