package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
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
    private List<Item> items;

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
    private Object data;
}