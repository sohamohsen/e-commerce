package com.task.ecommerce.user.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {
    private Integer orderId;
    private String status;
    private BigDecimal totalAmount;
}