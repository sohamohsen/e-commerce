package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymobTransactionResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("pending")
    private Boolean pending;

    @JsonProperty("amount_cents")
    private Integer amountCents;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("is_auth")
    private Boolean isAuth;

    @JsonProperty("is_capture")
    private Boolean isCapture;

    @JsonProperty("is_standalone_payment")
    private Boolean isStandalonePayment;

    @JsonProperty("is_voided")
    private Boolean isVoided;

    @JsonProperty("is_refunded")
    private Boolean isRefunded;

    @JsonProperty("is_3d_secure")
    private Boolean is3dSecure;

    @JsonProperty("integration_id")
    private Integer integrationId;

    @JsonProperty("profile_id")
    private Integer profileId;

    @JsonProperty("has_parent_transaction")
    private Boolean hasParentTransaction;

    @JsonProperty("order")
    private Order order;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("transaction_processed_callback_responses")
    private List<Object> transactionProcessedCallbackResponses;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("source_data")
    private SourceData sourceData;

    @JsonProperty("api_source")
    private String apiSource;

    @JsonProperty("terminal_id")
    private String terminalId;

    @JsonProperty("merchant_commission")
    private Object merchantCommission;

    @JsonProperty("installment")
    private String installment;

    @JsonProperty("discount_details")
    private List<Object> discountDetails;

    @JsonProperty("is_void")
    private Boolean isVoid;

    @JsonProperty("is_refund")
    private Boolean isRefund;

    @JsonProperty("data")
    private PaymentDataDto data;

    @JsonProperty("is_hidden")
    private Boolean isHidden;

    @JsonProperty("payment_key_claims")
    private String paymentKeyClaims;

    @JsonProperty("error_occured")
    private Boolean errorOccurred;

    @JsonProperty("is_live")
    private Boolean isLive;

    @JsonProperty("other_endpoint_reference")
    private String otherEndpointReference;

    @JsonProperty("refunded_amount_cents")
    private Integer refundedAmountCents;

    @JsonProperty("source_id")
    private Integer sourceId;

    @JsonProperty("is_captured")
    private Boolean isCaptured;

    @JsonProperty("captured_amount")
    private Integer capturedAmount;

    @JsonProperty("merchant_staff_tag")
    private String merchantStaffTag;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("is_settled")
    private Boolean isSettled;

    @JsonProperty("bill_balanced")
    private Boolean billBalanced;

    @JsonProperty("is_bill")
    private Boolean isBill;

    @JsonProperty("owner")
    private Integer owner;

    @JsonProperty("parent_transaction")
    private Long parentTransaction;
}