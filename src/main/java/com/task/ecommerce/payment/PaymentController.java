package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.payment.dto.TransactionWrapperDto;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for initiating order payments and Paymob webhook callbacks")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymobPropertiesConfig paymobProperties;
    private final PaymobHmacService paymobHmacService;

    @Operation(summary = "Initiate order payment", description = "Generates a Paymob client secret for checkout payment processing.")
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

    @Operation(summary = "Paymob webhook listener", description = "Callback endpoint invoked by Paymob payment gateway to process payment status updates.")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody TransactionWrapperDto rawBody,
            @RequestParam String hmac) {
        System.out.println("start receiving.");
        paymobHmacService.verifyAndExtract(rawBody, hmac);

        paymentService.processWebhook(rawBody);

        return ResponseEntity.ok().build();
    }

}
