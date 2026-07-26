package com.task.ecommerce.event;

import com.task.ecommerce.entity.enums.OrderStatus;
import lombok.Getter;

@Getter
public class OrderStatusChangedEvent {

    private final Integer orderId;
    private final Integer userId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;

    public OrderStatusChangedEvent(Integer orderId, Integer userId, OrderStatus oldStatus, OrderStatus newStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}