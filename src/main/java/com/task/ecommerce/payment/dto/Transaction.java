package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Transaction {

    private Long id;

    private Integer amountCents;

    private String createdAt;

    private String currency;

    private Boolean errorOccured;

    private Boolean hasParentTransaction;

    private Integer integrationId;

    @JsonProperty("is_3d_secure")
    private Boolean is3dSecure;

    private Boolean isAuth;

    private Boolean isCapture;

    private Boolean isRefunded;

    private Boolean isStandalonePayment;

    private Boolean isVoided;

    private Boolean pending;

    private Boolean success;

    private Integer owner;

    private Order order;

    private SourceData sourceData;
}