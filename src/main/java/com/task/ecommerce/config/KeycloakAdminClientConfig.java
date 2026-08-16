package com.task.ecommerce.config;

import com.task.ecommerce.config.properties.KeycloakAdminProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminClientConfig {

    private final KeycloakAdminProperties properties;

    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @PostConstruct
    public void checkConfig() {

        System.out.println(
                "SERVER = " + properties.getServerUrl()
        );

        System.out.println(
                "REALM = " + properties.getRealm()
        );

        System.out.println(
                "CLIENT = " + properties.getClientId()
        );

        System.out.println(
                "SECRET EXISTS = "
                        + (properties.getClientSecret() != null)
        );

        System.out.println(
                "SECRET LENGTH = "
                        + (properties.getClientSecret() == null
                        ? 0
                        : properties.getClientSecret().length())
        );
    }
}