package com.task.ecommerce.payment;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private String name;

    private int amount;

    private int quantity;
}