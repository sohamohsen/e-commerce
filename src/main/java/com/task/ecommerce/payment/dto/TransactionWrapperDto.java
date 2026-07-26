package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionWrapperDto {

    @JsonProperty("type")
    private String type;

    @JsonProperty("obj")
    private PaymentTransactionDto obj;

    @JsonProperty("issuer_bank")
    private Object issuerBank;

    @JsonProperty("transaction_processed_callback_responses")
    private Object transactionProcessedCallbackResponses;

    // ==================== الفئات الداخلية ====================

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentTransactionDto {

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
        private OrderDto order;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("transaction_processed_callback_responses")
        private List<Object> transactionProcessedCallbackResponses;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("source_data")
        private SourceDataDto sourceData;

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
        private PaymentKeyClaimsDto paymentKeyClaims;

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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderDto {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("delivery_needed")
        private Boolean deliveryNeeded;

        @JsonProperty("merchant")
        private MerchantDto merchant;

        @JsonProperty("collector")
        private String collector;

        @JsonProperty("amount_cents")
        private Integer amountCents;

        @JsonProperty("shipping_data")
        private ShippingDataDto shippingData;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("is_payment_locked")
        private Boolean isPaymentLocked;

        @JsonProperty("is_return")
        private Boolean isReturn;

        @JsonProperty("is_cancel")
        private Boolean isCancel;

        @JsonProperty("is_returned")
        private Boolean isReturned;

        @JsonProperty("is_canceled")
        private Boolean isCanceled;

        @JsonProperty("merchant_order_id")
        private String merchantOrderId;

        @JsonProperty("wallet_notification")
        private String walletNotification;

        @JsonProperty("paid_amount_cents")
        private Integer paidAmountCents;

        @JsonProperty("notify_user_with_email")
        private Boolean notifyUserWithEmail;

        @JsonProperty("items")
        private List<ItemDto> items;

        @JsonProperty("order_url")
        private String orderUrl;

        @JsonProperty("commission_fees")
        private Object commissionFees;

        @JsonProperty("delivery_fees_cents")
        private Integer deliveryFeesCents;

        @JsonProperty("delivery_vat_cents")
        private Integer deliveryVatCents;

        @JsonProperty("payment_method")
        private String paymentMethod;

        @JsonProperty("merchant_staff_tag")
        private String merchantStaffTag;

        @JsonProperty("api_source")
        private String apiSource;

        @JsonProperty("data")
        private Map<String, Object> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MerchantDto {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("phones")
        private List<String> phones;

        @JsonProperty("company_emails")
        private List<String> companyEmails;

        @JsonProperty("company_name")
        private String companyName;

        @JsonProperty("state")
        private String state;

        @JsonProperty("country")
        private String country;

        @JsonProperty("city")
        private String city;

        @JsonProperty("postal_code")
        private String postalCode;

        @JsonProperty("street")
        private String street;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingDataDto {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("street")
        private String street;

        @JsonProperty("building")
        private String building;

        @JsonProperty("floor")
        private String floor;

        @JsonProperty("apartment")
        private String apartment;

        @JsonProperty("city")
        private String city;

        @JsonProperty("state")
        private String state;

        @JsonProperty("country")
        private String country;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("postal_code")
        private String postalCode;

        @JsonProperty("extra_description")
        private String extraDescription;

        @JsonProperty("shipping_method")
        private String shippingMethod;

        @JsonProperty("order_id")
        private Long orderId;

        @JsonProperty("order")
        private Long order;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemDto {

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("amount_cents")
        private Integer amountCents;

        @JsonProperty("quantity")
        private Integer quantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SourceDataDto {

        @JsonProperty("type")
        private String type;

        @JsonProperty("pan")
        private String pan;

        @JsonProperty("sub_type")
        private String subType;

        @JsonProperty("tenure")
        private String tenure;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentDataDto {

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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MigsOrderDto {

        @JsonProperty("acceptPartialAmount")
        private Boolean acceptPartialAmount;

        @JsonProperty("amount")
        private Integer amount;

        @JsonProperty("authenticationStatus")
        private String authenticationStatus;

        @JsonProperty("chargeback")
        private ChargebackDto chargeback;

        @JsonProperty("creationTime")
        private String creationTime;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("description")
        private String description;

        @JsonProperty("id")
        private String id;

        @JsonProperty("lastUpdatedTime")
        private String lastUpdatedTime;

        @JsonProperty("merchantAmount")
        private Integer merchantAmount;

        @JsonProperty("merchantCategoryCode")
        private String merchantCategoryCode;

        @JsonProperty("merchantCurrency")
        private String merchantCurrency;

        @JsonProperty("status")
        private String status;

        @JsonProperty("totalAuthorizedAmount")
        private Integer totalAuthorizedAmount;

        @JsonProperty("totalCapturedAmount")
        private Integer totalCapturedAmount;

        @JsonProperty("totalRefundedAmount")
        private Integer totalRefundedAmount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChargebackDto {

        @JsonProperty("amount")
        private Integer amount;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MigsTransactionDto {

        @JsonProperty("acquirer")
        private AcquirerDto acquirer;

        @JsonProperty("amount")
        private Integer amount;

        @JsonProperty("authenticationStatus")
        private String authenticationStatus;

        @JsonProperty("authorizationCode")
        private String authorizationCode;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("id")
        private String id;

        @JsonProperty("receipt")
        private String receipt;

        @JsonProperty("source")
        private String source;

        @JsonProperty("stan")
        private String stan;

        @JsonProperty("terminal")
        private String terminal;

        @JsonProperty("type")
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AcquirerDto {

        @JsonProperty("batch")
        private Integer batch;

        @JsonProperty("date")
        private String date;

        @JsonProperty("id")
        private String id;

        @JsonProperty("merchantId")
        private String merchantId;

        @JsonProperty("settlementDate")
        private String settlementDate;

        @JsonProperty("timeZone")
        private String timeZone;

        @JsonProperty("transactionId")
        private String transactionId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentKeyClaimsDto {

        @JsonProperty("extra")
        private Map<String, Object> extra;

        @JsonProperty("user_id")
        private Integer userId;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("order_id")
        private Long orderId;

        @JsonProperty("amount_cents")
        private Integer amountCents;

        @JsonProperty("billing_data")
        private BillingDataDto billingData;

        @JsonProperty("redirect_url")
        private String redirectUrl;

        @JsonProperty("integration_id")
        private Integer integrationId;

        @JsonProperty("lock_order_when_paid")
        private Boolean lockOrderWhenPaid;

        @JsonProperty("next_payment_intention")
        private String nextPaymentIntention;

        @JsonProperty("single_payment_attempt")
        private Boolean singlePaymentAttempt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillingDataDto {

        @JsonProperty("city")
        private String city;

        @JsonProperty("email")
        private String email;

        @JsonProperty("floor")
        private String floor;

        @JsonProperty("state")
        private String state;

        @JsonProperty("street")
        private String street;

        @JsonProperty("country")
        private String country;

        @JsonProperty("building")
        private String building;

        @JsonProperty("apartment")
        private String apartment;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("postal_code")
        private String postalCode;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("extra_description")
        private String extraDescription;
    }
}