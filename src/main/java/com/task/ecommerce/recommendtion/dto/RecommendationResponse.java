package com.task.ecommerce.recommendtion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationResponse(
        @JsonProperty("productId") Integer productId,
        @JsonProperty("reason") String reason
) {}