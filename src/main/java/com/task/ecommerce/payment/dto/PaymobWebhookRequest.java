package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymobWebhookRequest {

    private String type;

    private Transaction obj;
}