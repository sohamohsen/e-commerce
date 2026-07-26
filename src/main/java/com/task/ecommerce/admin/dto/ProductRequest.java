package com.task.ecommerce.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull
    private Integer categoryId;

    private String imageUrl;

    @NotNull
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private boolean isActive;
}