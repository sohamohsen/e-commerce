package com.task.ecommerce.payment;

import com.task.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @Scheduled(fixedRate = 600000)
    public void expirePayments() {

        log.info("Checking expired orders and payments...");

        orderService.expireUnpaidOrders();
        paymentService.expirePendingPayments();
    }
}
