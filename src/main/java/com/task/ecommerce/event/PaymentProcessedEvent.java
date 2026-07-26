package com.task.ecommerce.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentProcessedEvent {

    private final Integer orderId;
    private final Integer userId;
    private final boolean success;
    private final BigDecimal amount;

    public PaymentProcessedEvent(Integer orderId, Integer userId, boolean success, BigDecimal amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.success = success;
        this.amount = amount;
    }
}