package com.task.ecommerce.admin;

import com.task.ecommerce.chat.AdminPresence;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.ConversationStatus;
import com.task.ecommerce.repository.ConversationRepository;
import com.task.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAvailabilityService {

    private final AdminPresenceService adminPresenceService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    /**
     * Returns the first admin that:
     * 1- Connected via WebSocket
     * 2- Enabled "Available for Chat"
     * 3- Doesn't have an ACTIVE conversation
     */
    public Optional<User> findAvailableAdmin() {

        for (AdminPresence presence : adminPresenceService.getAvailableAdmins()) {

            boolean busy = conversationRepository.existsByAdminIdAndStatus(
                    presence.getAdminId(),
                    ConversationStatus.ACTIVE
            );

            if (!busy) {
                return userRepository.findById(presence.getAdminId());
            }
        }

        return Optional.empty();
    }
}