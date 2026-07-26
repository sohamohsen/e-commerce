package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class PaymentDataDto {
    @JsonProperty("gateway_integration_pk")
    private Integer gatewayIntegrationPk;

    @JsonProperty("klass")
    private String klass;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("migs_order")
    private MigsOrderDto migsOrder;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("migs_result")
    private String migsResult;

    @JsonProperty("migs_transaction")
    private MigsTransactionDto migsTransaction;

    @JsonProperty("txn_response_code")
    private String txnResponseCode;

    @JsonProperty("acq_response_code")
    private String acqResponseCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("merchant_txn_ref")
    private String merchantTxnRef;

    @JsonProperty("order_info")
    private String orderInfo;

    @JsonProperty("receipt_no")
    private String receiptNo;

    @JsonProperty("transaction_no")
    private String transactionNo;

    @JsonProperty("batch_no")
    private Integer batchNo;

    @JsonProperty("authorize_id")
    private String authorizeId;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("card_num")
    private String cardNum;

    @JsonProperty("secure_hash")
    private String secureHash;

    @JsonProperty("avs_result_code")
    private String avsResultCode;

    @JsonProperty("avs_acq_response_code")
    private String avsAcqResponseCode;

    @JsonProperty("captured_amount")
    private Integer capturedAmount;

    @JsonProperty("authorised_amount")
    private Integer authorisedAmount;

    @JsonProperty("refunded_amount")
    private Integer refundedAmount;

    @JsonProperty("acs_eci")
    private String acsEci;
}