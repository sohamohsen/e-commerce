package com.task.ecommerce.admin.dto;

import com.task.ecommerce.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
