package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.exception.BadRequestException;
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
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentInitiationTest {

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
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private PaymentService paymentService;

    @Test
    void initiatePaymentRejectsAnOrderThatIsAlreadyInPaymentFlow() {
        Order order = Order.builder().id(10).userId(7).status(OrderStatus.PENDING_PAYMENT).build();
        when(orderRepository.findById(10)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> paymentService.initiatePayment(10, 7));

        verify(billRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(paymobClient, never()).createIntention(org.mockito.ArgumentMatchers.any());
    }
}
