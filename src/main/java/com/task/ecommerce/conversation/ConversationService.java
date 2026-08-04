package com.task.ecommerce.conversation;

import com.task.ecommerce.entity.Conversation;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.ConversationStatus;
import com.task.ecommerce.entity.enums.Role;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.exception.ForbiddenException;
import com.task.ecommerce.exception.NotFoundException;
import com.task.ecommerce.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    @Transactional
    public Conversation startConversation(Integer customerId) {

        Optional<Conversation> existing =
                conversationRepository.findByCustomerIdAndStatusIn(
                        customerId,
                        List.of(
                                ConversationStatus.WAITING,
                                ConversationStatus.ACTIVE
                        )
                );

        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conversation = Conversation.builder()
                .customerId(customerId)
                .status(ConversationStatus.WAITING)
                .build();

        return conversationRepository.save(conversation);
    }

    public List<Conversation> getWaitingConversations() {

        return conversationRepository
                .findAllByStatusOrderByCreatedAtAsc(
                        ConversationStatus.WAITING
                );
    }

    public Conversation getCurrentConversation(User user) {
        if (user.getRole() == Role.CUSTOMER) {
            return conversationRepository.findFirstByCustomerIdAndStatusInOrderByUpdatedAtDesc(
                    user.getId(), List.of(ConversationStatus.WAITING, ConversationStatus.ACTIVE))
                    .orElse(null);
        }
        return conversationRepository.findFirstByAdminIdAndStatusOrderByUpdatedAtDesc(
                user.getId(), ConversationStatus.ACTIVE).orElse(null);
    }

    @Transactional
    public Conversation acceptConversation(
            Long conversationId,
            Integer adminId
    ) {

        Conversation conversation =
                conversationRepository
                        .findByIdForUpdate(conversationId)
                        .orElseThrow(() -> new NotFoundException("Conversation not found."));

        if (conversation.getStatus() != ConversationStatus.WAITING) {

            throw new BadRequestException(
                    "Conversation already assigned."
            );
        }

        boolean adminBusy =
                conversationRepository.existsByAdminIdAndStatus(
                        adminId,
                        ConversationStatus.ACTIVE
                );

        if (adminBusy) {

            throw new BadRequestException(
                    "You already have an active conversation."
            );
        }

        conversation.setAdminId(adminId);

        conversation.setStatus(
                ConversationStatus.ACTIVE
        );

        return conversationRepository.save(conversation);
    }

    @Transactional
    public Conversation closeConversation(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findByIdForUpdate(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found."));
        boolean participant = conversation.getCustomerId().equals(user.getId())
                || (conversation.getAdminId() != null && conversation.getAdminId().equals(user.getId()));
        if (!participant) {
            throw new ForbiddenException("You do not have access to this conversation.");
        }
        if (conversation.getStatus() == ConversationStatus.CLOSED) {
            return conversation;
        }
        conversation.setStatus(ConversationStatus.CLOSED);
        return conversationRepository.save(conversation);
    }

}
