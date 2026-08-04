package com.task.ecommerce.admin;

import com.task.ecommerce.chat.AdminPresence;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminPresenceService {

    /**
     * key = adminId
     */
    private final Map<Integer, AdminPresence> adminsById = new ConcurrentHashMap<>();

    /**
     * key = websocket sessionId
     * value = adminId
     */
    private final Map<String, Integer> adminsBySession = new ConcurrentHashMap<>();

    /**
     * Called when websocket connection is established.
     */
    public void connect(Integer adminId, String sessionId) {

        AdminPresence presence = AdminPresence.builder()
                .adminId(adminId)
                .sessionId(sessionId)
                .availableForChat(false)
                .build();

        adminsById.put(adminId, presence);
        adminsBySession.put(sessionId, adminId);
    }

    /**
     * Called when websocket disconnects.
     */
    public void disconnect(String sessionId) {

        Integer adminId = adminsBySession.remove(sessionId);

        if (adminId != null) {
            adminsById.remove(adminId);
        }
    }

    public void setAvailable(Integer adminId) {

        AdminPresence presence = adminsById.get(adminId);

        if (presence != null) {
            presence.setAvailableForChat(true);
        }
    }

    public void makeAvailable(Integer adminId) {
        setAvailable(adminId);
    }

    public void setUnavailable(Integer adminId) {

        AdminPresence presence = adminsById.get(adminId);

        if (presence != null) {
            presence.setAvailableForChat(false);
        }
    }

    public void makeUnavailable(Integer adminId) {
        setUnavailable(adminId);
    }

    public boolean isAvailable(Integer adminId) {

        AdminPresence presence = adminsById.get(adminId);

        return presence != null && presence.isAvailableForChat();
    }

    public Optional<AdminPresence> get(Integer adminId) {

        return Optional.ofNullable(adminsById.get(adminId));
    }

    public Collection<AdminPresence> getConnectedAdmins() {

        return adminsById.values();
    }

    public Collection<AdminPresence> getAvailableAdmins() {

        return adminsById.values()
                .stream()
                .filter(AdminPresence::isAvailableForChat)
                .toList();
    }
}