package com.task.ecommerce.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymobRefundRequest {
    private String transactionId;
    private String amountCents;
}
