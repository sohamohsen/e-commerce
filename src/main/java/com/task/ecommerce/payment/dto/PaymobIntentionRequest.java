package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymobIntentionRequest {

    private Long amount;
    private String currency;

    @JsonProperty("payment_methods")
    private List<Long> paymentMethods;

    @JsonProperty("merchant_order_id")
    private String merchantOrderId;

    private Customer customer;

    @JsonProperty("billing_data")
    private BillingData billingData;

    private List<Item> items;

    private Map<String, Object> extras;

    private Integer expiration;

    @JsonProperty("notification_url")
    private String notificationUrl;

    @JsonProperty("redirection_url")
    private String redirectionUrl;
}