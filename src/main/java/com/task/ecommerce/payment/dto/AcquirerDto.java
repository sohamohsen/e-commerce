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
public class AcquirerDto {
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
