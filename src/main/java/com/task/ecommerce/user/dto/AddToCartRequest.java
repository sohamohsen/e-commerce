package com.task.ecommerce.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}