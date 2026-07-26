package com.task.ecommerce.payment.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PaymentWebhookData {

    String paymobOrderId;

    String transactionId;

    boolean success;

    LocalDateTime paidAt;
}