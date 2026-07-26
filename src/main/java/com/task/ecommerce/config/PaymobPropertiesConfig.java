package com.task.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paymob")
@Getter
@Setter
public class PaymobProperties {
    private String apiKey;
    private String publicKey;
    private String secretKey;
    private String integrationId;
    private String hmac;
}