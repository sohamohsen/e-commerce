package com.task.ecommerce.payment.dto;

import lombok.Data;

@Data
public class PaymobAuthResponse {

    private Profile profile;
    private String token;
}