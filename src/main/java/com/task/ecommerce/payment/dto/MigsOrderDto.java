package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigsOrderDto {
    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("chargeback")
    private ChargebackDto chargeback;

    @JsonProperty("creationTime")
    private String creationTime;

    @JsonProperty("currency")
    private String currency;

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

    @JsonProperty("totalDisbursedAmount")
    private Object totalDisbursedAmount;

    @JsonProperty("totalRefundedAmount")
    private Integer totalRefundedAmount;
}
