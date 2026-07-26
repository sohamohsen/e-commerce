package com.task.ecommerce.utils;

import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class OrderStatusValidator {

    private final static Map<OrderStatus, Set<OrderStatus>> TRANSITION = Map.of(
            OrderStatus.PAID , Set.of(OrderStatus.PREPARED),
            OrderStatus.PREPARED , Set.of(OrderStatus.SHIPPED),
            OrderStatus.DELIVERED , Set.of(OrderStatus.DELIVERED)
    );

    public void validateTransition (OrderStatus current, OrderStatus target){
        Set<OrderStatus> allowed = TRANSITION.get(current);

        if(allowed == null || !allowed.contains(target)){
            throw new BadRequestException(
                    "Cannot change order status from " + current + " to " + target);

        }
    }
}
