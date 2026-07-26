package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)

public class SourceData {
    @JsonProperty("type")
    private String type;

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("tenure")
    private String tenure;
}