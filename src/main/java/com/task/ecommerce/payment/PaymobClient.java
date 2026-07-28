package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.payment.dto.PaymobAuthRequest;
import com.task.ecommerce.payment.dto.PaymobAuthResponse;
import com.task.ecommerce.payment.dto.PaymobIntentionRequest;
import com.task.ecommerce.payment.dto.PaymobIntentionResponse;
import com.task.ecommerce.payment.dto.PaymobRefundRequest;
import com.task.ecommerce.payment.dto.PaymobTransactionResponse;
import com.task.ecommerce.payment.dto.PaymobVoidRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymobClient {

    private final WebClient paymobWebClient;
    private final PaymobPropertiesConfig properties;

    public PaymobAuthResponse createToken() {
        return paymobWebClient.post()
                .uri("/api/auth/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymobAuthRequest(properties.getApiKey()))
                .retrieve()
                .bodyToMono(PaymobAuthResponse.class)
                .block();
    }

    public PaymobIntentionResponse createIntention(PaymobIntentionRequest request) {

        log.info("Calling Paymob Intention API...");
        log.info("Integration ID: {}", properties.getIntegrationId());
        log.info("Notification URL: {}", properties.getNotificationUrl());
        log.info("Redirection URL: {}", properties.getRedirectionUrl());

        return postWithSecret("/v1/intention/", request, PaymobIntentionResponse.class);
    }

    public PaymobTransactionResponse createVoid(PaymobVoidRequest request) {
        return postWithSecret("/api/acceptance/void_refund/void", request, PaymobTransactionResponse.class);
    }

    public PaymobTransactionResponse createRefund(PaymobRefundRequest request) {
        return postWithSecret("/api/acceptance/void_refund/refund", request, PaymobTransactionResponse.class);
    }

    private <T> T postWithSecret(String path, Object request, Class<T> responseType) {

        log.info("========== Paymob Request ==========");
        log.info("Path: {}", path);
        log.info("Request: {}", request);

        return paymobWebClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Token " + properties.getSecretKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchangeToMono(response -> {

                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(responseType);
                    }

                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("========== Paymob Error ==========");
                                log.error("Status: {}", response.statusCode());
                                log.error("Response Body: {}", body);

                                return Mono.error(new RuntimeException(body));
                            });
                })
                .block();
    }
}
