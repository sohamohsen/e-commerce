package com.task.ecommerce.service;

import com.task.ecommerce.entity.CartItem;
import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.OrderItem;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.repository.CartItemRepository;
import com.task.ecommerce.repository.OrderItemRepository;
import com.task.ecommerce.repository.OrderRepository;
import com.task.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void checkoutReservesStockAndClearsCart() {
        CartItem cartItem = CartItem.builder()
                .id(1)
                .userId(7)
                .productId(11)
                .quantity(2)
                .build();

        Product product = Product.builder()
                .id(11)
                .name("Keyboard")
                .price(new BigDecimal("150.00"))
                .quantity(5)
                .isActive(true)
                .version(1)
                .build();

        when(cartItemRepository.findByUserId(7)).thenReturn(List.of(cartItem));
        when(productRepository.findById(11)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(55);
            return savedOrder;
        });

        var orderResponse = orderService.checkout(7);

        assertEquals(55, orderResponse.getOrderId());
        assertEquals("PENDING", orderResponse.getStatus());
        assertEquals(new BigDecimal("300.00"), orderResponse.getTotalAmount());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(2)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getAllValues().get(1);
        assertEquals(7, savedOrder.getUserId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(new BigDecimal("300.00"), savedOrder.getTotalAmount());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OrderItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<OrderItem> itemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(itemCaptor.capture());
        OrderItem savedItem = itemCaptor.getValue();
        assertEquals(55, savedItem.getOrderId());
        assertEquals(11, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("150.00"), savedItem.getUnitPrice());
        assertNotNull(savedItem);

        verify(cartItemRepository).deleteByUserId(7);
        verify(productRepository).save(product);
        assertEquals(3, product.getQuantity());
    }
}
