package com.task.ecommerce.conversation;

import com.task.ecommerce.entity.ChatMessage;
import com.task.ecommerce.entity.Conversation;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.ConversationStatus;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.exception.ForbiddenException;
import com.task.ecommerce.exception.NotFoundException;
import com.task.ecommerce.repository.ChatMessageRepository;
import com.task.ecommerce.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long conversationId, User user) {
        authorizeParticipant(findConversation(conversationId), user);
        return chatMessageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public ChatMessage sendMessage(Long conversationId, String content, User user) {
        Conversation conversation = findConversation(conversationId);
        authorizeParticipant(conversation, user);

        if (conversation.getStatus() != ConversationStatus.ACTIVE) {
            throw new BadRequestException("Messages can only be sent in an active conversation.");
        }

        ChatMessage message = ChatMessage.builder()
                .conversationId(conversationId)
                .senderId(user.getId().longValue())
                .content(content.trim())
                .build();
        return chatMessageRepository.save(message);
    }

    private Conversation findConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found."));
    }

    private void authorizeParticipant(Conversation conversation, User user) {
        boolean customer = conversation.getCustomerId().equals(user.getId());
        boolean assignedAdmin = conversation.getAdminId() != null
                && conversation.getAdminId().equals(user.getId());
        if (!customer && !assignedAdmin) {
            throw new ForbiddenException("You do not have access to this conversation.");
        }
    }
}
