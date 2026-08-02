package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.OrderItem;
import com.task.ecommerce.entity.Payment;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.entity.enums.PaymentStatus;
import com.task.ecommerce.repository.BillRepository;
import com.task.ecommerce.repository.OrderItemRepository;
import com.task.ecommerce.repository.OrderRepository;
import com.task.ecommerce.repository.PaymentRepository;
import com.task.ecommerce.repository.ProductRepository;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.notification.EmailService;
import com.task.ecommerce.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceCancellationTest {

    @Mock private PaymobClient paymobClient;
    @Mock private PaymobPropertiesConfig paymobPropertiesConfig;
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private BillRepository billRepository;
    @Mock private InvoiceService invoiceService;
    @Mock private EmailService emailService;

    @InjectMocks private PaymentService paymentService;

    @Test
    void cancelUnpaidOrderRestoresStockImmediately() {
        Order order = Order.builder().id(10).status(OrderStatus.PENDING).build();
        OrderItem item = OrderItem.builder().productId(7).quantity(2).build();
        Product product = Product.builder().id(7).quantity(3).build();

        when(orderRepository.findById(10)).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(10)).thenReturn(Optional.empty());
        when(orderItemRepository.findByOrderId(10)).thenReturn(List.of(item));
        when(productRepository.findById(7)).thenReturn(Optional.of(product));

        paymentService.cancelOrder(10, 4, "Customer requested cancellation");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, product.getQuantity());
        assertEquals(4, order.getCancelledBy());
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelPaidOrderRequestsRefundWithoutRestoringStockBeforeWebhookConfirmation() {
        Order order = Order.builder()
                .id(10)
                .status(OrderStatus.PAID)
                .totalAmount(new BigDecimal("125.50"))
                .build();
        Payment payment = Payment.builder()
                .id(20)
                .orderId(10)
                .transactionId("paymob-transaction")
                .status(PaymentStatus.SUCCESS)
                .build();

        when(orderRepository.findById(10)).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(10)).thenReturn(Optional.of(payment));

        paymentService.cancelOrder(10, 4, "Item unavailable");

        assertEquals(OrderStatus.CANCELLATION_REQUESTED, order.getStatus());
        assertEquals(PaymentStatus.REFUND_REQUESTED, payment.getStatus());
        verify(paymobClient).createRefund(any());
        verify(productRepository, never()).save(any(Product.class));
    }
}
