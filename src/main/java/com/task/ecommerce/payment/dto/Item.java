package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount_cents")
    private Integer amountCents;

    @JsonProperty("quantity")
    private Integer quantity;
}