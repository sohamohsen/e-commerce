package com.task.ecommerce.config.properties;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.admin")
@Data
public class KeycloakAdminProperties {
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

}
