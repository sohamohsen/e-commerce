package com.task.ecommerce.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequest {

    @NotNull
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}