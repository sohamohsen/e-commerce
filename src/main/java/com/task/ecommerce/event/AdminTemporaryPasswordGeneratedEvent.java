package com.task.ecommerce.event;

import lombok.Getter;

@Getter
public class AdminTemporaryPasswordGeneratedEvent {

    private final Integer adminUserId;
    private final String email;
    private final String temporaryPassword;

    public AdminTemporaryPasswordGeneratedEvent(Integer adminUserId, String email, String temporaryPassword) {
        this.adminUserId = adminUserId;
        this.email = email;
        this.temporaryPassword = temporaryPassword;
    }

}