package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymobIntentionResponse {

    @JsonProperty("client_secret")
    private String clientSecret;

    private String id;

    @JsonProperty("intention_order_id")
    private Long intentionOrderId;
}