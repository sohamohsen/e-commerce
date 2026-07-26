package com.task.ecommerce.payment;

import com.task.ecommerce.config.PaymobPropertiesConfig;
import com.task.ecommerce.payment.dto.PaymobAuthRequest;
import com.task.ecommerce.payment.dto.PaymobAuthResponse;
import com.task.ecommerce.payment.dto.PaymobIntentionRequest;
import com.task.ecommerce.payment.dto.PaymobIntentionResponse;
import com.task.ecommerce.payment.dto.PaymobRefundRequest;
import com.task.ecommerce.payment.dto.PaymobTransactionResponse;
import com.task.ecommerce.payment.dto.PaymobVoidRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
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
        return postWithSecret("/v1/intention/", request, PaymobIntentionResponse.class);
    }

    public PaymobTransactionResponse createVoid(PaymobVoidRequest request) {
        return postWithSecret("/api/acceptance/void_refund/void", request, PaymobTransactionResponse.class);
    }

    public PaymobTransactionResponse createRefund(PaymobRefundRequest request) {
        return postWithSecret("/api/acceptance/void_refund/refund", request, PaymobTransactionResponse.class);
    }

    private <T> T postWithSecret(String path, Object request, Class<T> responseType) {
        return paymobWebClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Token " + properties.getSecretKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
