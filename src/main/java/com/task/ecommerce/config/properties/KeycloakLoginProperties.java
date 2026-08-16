package com.task.ecommerce.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak.login")
public class KeycloakLoginProperties {

    private String serverUrl;
    private String clientId;
    private String clientSecret;
}