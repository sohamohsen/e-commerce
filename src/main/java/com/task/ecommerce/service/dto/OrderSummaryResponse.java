package com.task.ecommerce.service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderSummaryResponse {
    private Integer id;
    private Integer userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
