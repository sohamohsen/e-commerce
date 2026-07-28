package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.payment.dto.TransactionWrapperDto;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymobPropertiesConfig paymobProperties;
    private final PaymobHmacService paymobHmacService;

    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<?> initiatePayment(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal User user
    ) {
        String clientSecret = paymentService.initiatePayment(orderId, user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Payment initiated.")
                .data(Map.of(
                        "clientSecret", clientSecret,
                        "publicKey", paymobProperties.getPublicKey()
                ))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody TransactionWrapperDto rawBody,
            @RequestParam String hmac) {

        paymobHmacService.verifyAndExtract(rawBody, hmac);

        paymentService.processWebhook(rawBody);

        return ResponseEntity.ok().build();
    }

}
