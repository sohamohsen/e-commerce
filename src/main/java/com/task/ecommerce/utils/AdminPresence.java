package com.task.ecommerce.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPresence {

    private Integer adminId;

    /**
     * WebSocket Session Id
     */
    private String sessionId;

    /**
     * Admin turned ON "Available for Chat"
     */
    private boolean availableForChat;
}