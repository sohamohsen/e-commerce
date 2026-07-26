package com.task.ecommerce.service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceData {
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private LocalDateTime issuedAt;
    private String paymobOrderId;
    private String transactionId;
    private List<InvoiceLine> lines;
    private long totalAmountCents;
    private String currency;

    @Data
    @Builder
    public static class InvoiceLine {
        private String productName;
        private int quantity;
        private long unitPriceCents;
        private long lineTotalCents;
    }
}