package com.task.ecommerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    private Integer gatewayIntegrationPk;
    private String klass;
    private String createdAt;
    private Integer amount;
    private String currency;
    private MigsOrderDto migsOrder;
    private String merchant;
    private String migsResult;
    private MigsTransactionDto migsTransaction;
    private String txnResponseCode;
    private String acqResponseCode;
    private String message;
    private String merchantTxnRef;
    private String orderInfo;
    private String receiptNo;
    private String transactionNo;
    private Integer batchNo;
    private String authorizeId;
    private String cardType;
    private String cardNum;
    private String secureHash;
    private String avsResultCode;
    private String avsAcqResponseCode;
    private Integer capturedAmount;
    private Integer authorisedAmount;
    private Integer refundedAmount;
    private String acsEci;
}
