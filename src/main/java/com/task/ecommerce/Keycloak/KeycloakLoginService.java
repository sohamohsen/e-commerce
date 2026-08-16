package com.task.ecommerce.Keycloak;

import com.task.ecommerce.Keycloak.KeycloakTokenResponse;
import com.task.ecommerce.config.properties.KeycloakLoginProperties;
import com.task.ecommerce.exception.KeycloakAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakLoginService {

    private final WebClient webClient;

    private static final String REALM = "ecommerce";

    private final KeycloakLoginProperties properties;

    public KeycloakTokenResponse login(
            String username,
            String password
    ) {

        MultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();

        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("username", username);
        form.add("password", password);

        log.info("=== KEYCLOAK LOGIN START ===");
        log.info("Username: {}", username);
        log.info("Client ID: {}", properties.getClientId());
        log.info(
                "Keycloak URL: {}/realms/{}/protocol/openid-connect/token",
                properties.getServerUrl(),
                REALM
        );

        try {

            return webClient
                    .post()
                    .uri(
                            properties.getServerUrl()
                                    + "/realms/"
                                    + REALM
                                    + "/protocol/openid-connect/token"
                    )
                    .bodyValue(form)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {

                                                log.error(
                                                        "KEYCLOAK LOGIN 4xx | status={} | body={}",
                                                        response.statusCode(),
                                                        body
                                                );

                                                return Mono.error(
                                                        new KeycloakAuthenticationException(
                                                                "Keycloak authentication failed: "
                                                                        + body
                                                        )
                                                );
                                            })
                    )
                    .bodyToMono(KeycloakTokenResponse.class)
                    .block();

        } catch (KeycloakAuthenticationException ex) {

            log.error(
                    "KEYCLOAK AUTHENTICATION FAILED | username={} | message={}",
                    username,
                    ex.getMessage()
            );

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "KEYCLOAK LOGIN UNEXPECTED ERROR | username={}",
                    username,
                    ex
            );

            throw new RuntimeException(
                    "Keycloak authentication failed",
                    ex
            );
        }
    }

    public KeycloakTokenResponse refresh(String refreshToken) {

        MultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();

        form.add("grant_type", "refresh_token");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("refresh_token", refreshToken);

        log.info("=== KEYCLOAK REFRESH START ===");
        log.info("Client ID: {}", properties.getClientId());

        try {

            return webClient
                    .post()
                    .uri(
                            properties.getServerUrl()
                                    + "/realms/"
                                    + REALM
                                    + "/protocol/openid-connect/token"
                    )
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(form)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {

                                                log.warn(
                                                        "KEYCLOAK REFRESH FAILED | status={} | body={}",
                                                        response.statusCode(),
                                                        body
                                                );

                                                return Mono.error(
                                                        new KeycloakAuthenticationException(
                                                                "Refresh token is invalid or expired"
                                                        )
                                                );
                                            })
                    )
                    .bodyToMono(KeycloakTokenResponse.class)
                    .block();

        } catch (KeycloakAuthenticationException ex) {

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Unexpected error during Keycloak refresh",
                    ex
            );

            throw new RuntimeException(
                    "Keycloak refresh failed",
                    ex
            );
        }
    }

    public void logout(String refreshToken) {

        log.info("=== KEYCLOAK LOGOUT START ===");

        try {

            webClient
                    .post()
                    .uri(
                            properties.getServerUrl()
                                    + "/realms/"
                                    + REALM
                                    + "/protocol/openid-connect/logout"
                    )
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(
                            BodyInserters.fromFormData(
                                            "client_id",
                                            properties.getClientId()
                                    )
                                    .with(
                                            "client_secret",
                                            properties.getClientSecret()
                                    )
                                    .with(
                                            "refresh_token",
                                            refreshToken
                                    )
                    )
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {

                                                log.warn(
                                                        "KEYCLOAK LOGOUT FAILED | status={} | body={}",
                                                        response.statusCode(),
                                                        body
                                                );

                                                return Mono.error(
                                                        new KeycloakAuthenticationException(
                                                                "Keycloak logout failed"
                                                        )
                                                );
                                            })
                    )
                    .toBodilessEntity()
                    .block();

            log.info("Keycloak logout SUCCESS");

        } catch (KeycloakAuthenticationException ex) {

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Unexpected error during Keycloak logout",
                    ex
            );

            throw new RuntimeException(
                    "Keycloak logout failed",
                    ex
            );
        }
    }
}