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
public class MigsTransactionDto {
    @JsonProperty("acquirer")
    private AcquirerDto acquirer;

    @JsonProperty("amount")
    private Integer amount;

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