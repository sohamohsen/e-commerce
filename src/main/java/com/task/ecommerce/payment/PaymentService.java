package com.task.ecommerce.payment;

import com.task.ecommerce.config.PaymobPropertiesConfig;
import com.task.ecommerce.entity.*;
import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.BillStatus;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.entity.enums.PaymentStatus;
import com.task.ecommerce.event.PaymentProcessedEvent;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.payment.dto.*;
import com.task.ecommerce.repository.*;
import com.task.ecommerce.notification.EmailService;
import com.task.ecommerce.service.InvoiceService;
import com.task.ecommerce.service.dto.InvoiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymobClient paymobClient;
    private final PaymobPropertiesConfig paymobPropertiesConfig;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final BillRepository billRepository;
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void cancelOrder(Integer orderId, Integer adminId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found."));

        if (order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.CANCELLATION_REQUESTED
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("This order cannot be cancelled in its current status.");
        }

        order.setCancelledBy(adminId);
        order.setCancellationReason(reason);
        order.setCancelledAt(LocalDateTime.now());

        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId)
                .orElse(null);

        if (payment == null || payment.getStatus() == PaymentStatus.FAILED
                || payment.getStatus() == PaymentStatus.EXPIRED) {
            completeCancellation(order);
            return;
        }

        if (payment.getStatus() == PaymentStatus.INITIATED) {
            order.setStatus(OrderStatus.CANCELLATION_REQUESTED);
            orderRepository.save(order);
            return;
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.CAPTURED) {
            order.setStatus(OrderStatus.CANCELLATION_REQUESTED);
            payment.setStatus(PaymentStatus.REFUND_REQUESTED);
            orderRepository.save(order);
            paymentRepository.save(payment);
            requestRefund(payment, order);
            return;
        }

        throw new BadRequestException("A payment operation is already in progress for this order.");
    }

    @Transactional
    public String initiatePayment(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found."));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Order not found.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Payment can only be initiated for a pending order.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        long amountCents = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        Item item = Item.builder()
                .name("Order " + order.getId())
                .quantity(1)
                .amountCents(Math.toIntExact(amountCents))
                .description("Order Payment")
                .build();

        Bill bill = createPendingBill(order, user, amountCents);

        PaymobIntentionRequest request = PaymobIntentionRequest.builder()
                .amount(bill.getTotalAmountCents())
                .currency(bill.getCurrency())
                .paymentMethods(List.of(Long.valueOf(paymobPropertiesConfig.getIntegrationId())))
                .merchantOrderId(String.valueOf(order.getId()))
                .customer(Customer.builder()
                        .firstName(user.getName())
                        .lastName("N/A")
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .items(Collections.singletonList(item))
                .billingData(BillingData.builder()
                        .firstName(bill.getCustomerName())
                        .lastName("N/A")
                        .email(bill.getCustomerEmail())
                        .phone(bill.getCustomerPhone())
                        .country("EG")
                        .city("N/A")
                        .street("N/A")
                        .building("N/A")
                        .floor("N/A")
                        .apartment("N/A")
                        .postalCode("12345")
                        .build())
                .extras(Collections.emptyMap())
                .expiration(3600)
                .notificationUrl(paymobPropertiesConfig.getNotificationUrl())
                .redirectionUrl(paymobPropertiesConfig.getRedirectionUrl())
                .build();
        PaymobIntentionResponse response = paymobClient.createIntention(request);

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .paymobOrderId(String.valueOf(response.getIntentionOrderId()))
                .clientSecret(response.getClientSecret())
                .amount(order.getTotalAmount())
                .currency("EGP")
                .status(PaymentStatus.INITIATED)
                .build();
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);

        return response.getClientSecret();
    }

    private Bill createPendingBill(Order order, User user, long amountCents) {

        Bill bill = Bill.builder()
                .invoiceNumber("INV-ORDER-" + order.getId())
                .orderId(order.getId())
                .transactionId(null)
                .customerName(user.getName())
                .customerEmail(user.getEmail())
                .customerPhone(user.getPhone())
                .totalAmountCents(amountCents)
                .currency("EGP")
                .issuedAt(LocalDateTime.now())
                .status(BillStatus.PENDING)
                .build();

        return billRepository.save(bill);

    }

    @Transactional
    public void processWebhook(TransactionWrapperDto webhookData) {

        Payment payment = paymentRepository
                .findByPaymobOrderId(String.valueOf(webhookData.getObj().getOrder().getId()))
                .orElseThrow(() -> new BadRequestException("Payment not found"));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        payment.setTransactionId(String.valueOf(webhookData.getObj().getId()));

        // Refund callback
        if (Boolean.TRUE.equals(webhookData.getObj().getIsRefunded())) {

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            completeCancellationIfRequested(order);

            log.info("Payment {} refunded.", payment.getId());
            return;
        }

        // Void callback
        if (Boolean.TRUE.equals(webhookData.getObj().getIsVoid())) {

            payment.setStatus(PaymentStatus.VOIDED);
            paymentRepository.save(payment);

            completeCancellationIfRequested(order);

            log.info("Payment {} voided.", payment.getId());
            return;
        }

        // Capture callback
        if (Boolean.TRUE.equals(webhookData.getObj().getIsCaptured())) {

            payment.setStatus(PaymentStatus.CAPTURED);
            paymentRepository.save(payment);

            log.info("Payment {} captured.", payment.getId());
        }

        processPaymentResult(payment, order, webhookData);
    }

    private void compensateExpiredPayment(
            Payment payment,
            Order order) {

        PaymobVoidRequest request = PaymobVoidRequest.builder()
                .transactionId(payment.getTransactionId())
                .build();

        try {

            paymobClient.createVoid(request);

            log.info("Void requested for payment {}", payment.getId());

        } catch (Exception ex) {

            PaymobRefundRequest refund = PaymobRefundRequest.builder()
                    .transactionId(payment.getTransactionId())
                    .amountCents(
                            String.valueOf(order.getTotalAmount()
                                    .multiply(BigDecimal.valueOf(100))
                                    .longValueExact())
                    )
                    .build();

            paymobClient.createRefund(refund);

            log.info("Refund requested for payment {}", payment.getId());
        }
    }

    private void requestRefund(Payment payment, Order order) {
        if (payment.getTransactionId() == null || payment.getTransactionId().isBlank()) {
            throw new BadRequestException("Cannot refund a payment without a Paymob transaction id.");
        }

        PaymobRefundRequest refundRequest = PaymobRefundRequest.builder()
                .transactionId(payment.getTransactionId())
                .amountCents(String.valueOf(order.getTotalAmount()
                        .movePointRight(2)
                        .longValueExact()))
                .build();

        paymobClient.createRefund(refundRequest);
        log.info("Refund requested for payment {}.", payment.getId());
    }

    private void completeCancellationIfRequested(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLATION_REQUESTED) {
            completeCancellation(order);
        }
    }

    private void completeCancellation(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        restoreStock(order.getId());
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        markBillFailed(order.getId());
    }

    private void restoreStock(Integer orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found."));

            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public void expirePendingPayments() {

        LocalDateTime expiryTime = LocalDateTime.now().minusHours(1);

        List<Payment> expiredPayments =
                paymentRepository.findByStatusAndCreatedAtBefore(
                        PaymentStatus.INITIATED,
                        expiryTime
                );

        for (Payment payment : expiredPayments) {

            payment.setStatus(PaymentStatus.EXPIRED);

            Order order = orderRepository.findById(payment.getOrderId())
                    .orElseThrow();

            order.setStatus(OrderStatus.CANCELLED);

            restoreStock(order.getId());

            paymentRepository.save(payment);
            orderRepository.save(order);

            markBillFailed(order.getId());

            log.info("Payment for order {} expired.", order.getId());
        }
    }

    private void generateAndSendInvoice(Order order, Payment payment) {

        Bill bill = billRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new BadRequestException("Bill not found for order " + order.getId()));

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<InvoiceData.InvoiceLine> lines = items.stream()
                .map(item -> {
                    long unitPriceCents = item.getUnitPrice()
                            .movePointRight(2)
                            .longValueExact();
                    long lineTotalCents = Math.multiplyExact(unitPriceCents, item.getQuantity());
                    return InvoiceData.InvoiceLine.builder()
                            .productName("Product #" + item.getProductId())
                            .quantity(item.getQuantity())
                            .unitPriceCents(unitPriceCents)
                            .lineTotalCents(lineTotalCents)
                            .build();
                })
                .toList();

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found."));

        InvoiceData invoice = InvoiceData.builder()
                .invoiceNumber(bill.getInvoiceNumber())
                .customerName(user.getName())
                .customerEmail(user.getEmail())
                .issuedAt(payment.getPaidAt())
                .paymobOrderId(payment.getPaymobOrderId())
                .transactionId(payment.getTransactionId())
                .lines(lines)
                .totalAmountCents(lines.stream().mapToLong(InvoiceData.InvoiceLine::getLineTotalCents).sum())
                .currency("EGP")
                .build();

        String html = invoiceService.renderHtml(invoice);

        bill.setTransactionId(payment.getTransactionId());
        bill.setTotalAmountCents(invoice.getTotalAmountCents());
        bill.setIssuedAt(payment.getPaidAt());
        bill.setHtmlSnapshot(html);

        try {
            byte[] pdf = invoiceService.renderPdf(html);

            emailService.sendInvoiceEmail(
                    invoice.getCustomerEmail(),
                    "Your Invoice - Order " + invoice.getPaymobOrderId(),
                    html,
                    pdf,
                    invoice.getInvoiceNumber() + ".pdf"
            );

            bill.setStatus(BillStatus.SENT);
            bill.setEmailSentAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Failed to send invoice for order {}", order.getId(), e);
            bill.setStatus(BillStatus.FAILED);
        }

        billRepository.save(bill);
    }

    private void markBillFailed(Integer orderId) {
        billRepository.findByOrderId(orderId).ifPresent(bill -> {
            bill.setStatus(BillStatus.FAILED);
            billRepository.save(bill);
        });
    }

    private void processPaymentResult(Payment payment, Order order, TransactionWrapperDto webhookData) {

        if (webhookData.getObj().getSuccess()) {

            if (order.getStatus() == OrderStatus.CANCELLATION_REQUESTED) {
                payment.setStatus(PaymentStatus.REFUND_REQUESTED);
                paymentRepository.save(payment);
                requestRefund(payment, order);
                return;
            }

            if (payment.getStatus() == PaymentStatus.EXPIRED) {
                compensateExpiredPayment(payment, order);
                return;
            }

            if (payment.getStatus() != PaymentStatus.INITIATED
                    && payment.getStatus() != PaymentStatus.CAPTURED) {
                return;
            }

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(webhookData.getObj().getCreatedAt());
            order.setStatus(OrderStatus.PAID);

            paymentRepository.save(payment);
            orderRepository.save(order);

            eventPublisher.publishEvent(
                    new PaymentProcessedEvent(order.getId(), order.getUserId(), true, order.getTotalAmount())
            );

            generateAndSendInvoice(order, payment);

        } else {

            if (payment.getStatus() != PaymentStatus.INITIATED) {
                return;
            }

            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.FAILED);

            restoreStock(order.getId());

            paymentRepository.save(payment);
            orderRepository.save(order);

            markBillFailed(order.getId());

            eventPublisher.publishEvent(
                    new PaymentProcessedEvent(order.getId(), order.getUserId(), false, order.getTotalAmount())
            );
        }
    }
}
