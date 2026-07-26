package com.task.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDetailResponse {

    private Integer id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CustomerInfo customer;
    private List<OrderItemInfo> items;

    @Data
    @Builder
    public static class CustomerInfo {
        private Integer id;
        private String name;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    public static class OrderItemInfo {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}