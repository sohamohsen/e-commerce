package com.task.ecommerce.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationResponse(
        @JsonProperty("productId") Integer productId,
        @JsonProperty("reason") String reason
) {}