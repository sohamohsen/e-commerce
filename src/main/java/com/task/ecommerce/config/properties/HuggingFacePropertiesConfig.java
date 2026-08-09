package com.task.ecommerce.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "huggingface")
@Getter
@Setter
public class HuggingFacePropertiesConfig {
    private String apiKey;
    private String model;
    private String baseUrl;
}