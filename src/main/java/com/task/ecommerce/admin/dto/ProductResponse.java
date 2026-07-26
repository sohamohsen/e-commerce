package com.task.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Integer id;

    private Integer categoryId;
    private String categoryName;

    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private boolean isActive;
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer createdBy;
    private Integer updatedBy;

}
