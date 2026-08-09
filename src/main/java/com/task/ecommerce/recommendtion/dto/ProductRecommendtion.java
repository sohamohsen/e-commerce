package com.task.ecommerce.recommendtion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ProductRecommendtion {
    private int productId;
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
}
