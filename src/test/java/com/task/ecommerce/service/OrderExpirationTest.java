package com.task.ecommerce.service;

import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.OrderItem;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.event.OrderStatusChangedEvent;
import com.task.ecommerce.repository.CartItemRepository;
import com.task.ecommerce.repository.OrderItemRepository;
import com.task.ecommerce.repository.OrderRepository;
import com.task.ecommerce.repository.ProductRepository;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.utils.OrderStatusValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderExpirationTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderStatusValidator orderStatusValidator;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private OrderService orderService;

    @Test
    void expireUnpaidOrdersRestoresReservedStock() {
        Order order = Order.builder().id(10).userId(5).status(OrderStatus.PENDING).build();
        OrderItem item = OrderItem.builder().productId(7).quantity(2).build();
        Product product = Product.builder().id(7).quantity(3).build();

        when(orderRepository.findByStatusAndCreatedAtBefore(any(), any(LocalDateTime.class)))
                .thenReturn(List.of(order));
        when(orderItemRepository.findByOrderId(10)).thenReturn(List.of(item));
        when(productRepository.findById(7)).thenReturn(Optional.of(product));

        orderService.expireUnpaidOrders();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, product.getQuantity());
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }
}
