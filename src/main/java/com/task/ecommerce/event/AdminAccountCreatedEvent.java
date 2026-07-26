package com.task.ecommerce.event;

import lombok.Getter;

@Getter
public class AdminAccountCreatedEvent {

    private final Integer adminUserId;
    private final String email;
    private final String temporaryPassword;

    public AdminAccountCreatedEvent(Integer adminUserId, String email, String temporaryPassword) {
        this.adminUserId = adminUserId;
        this.email = email;
        this.temporaryPassword = temporaryPassword;
    }
}