package com.task.ecommerce.service;

import com.task.ecommerce.admin.dto.OrderDetailResponse;
import com.task.ecommerce.admin.dto.OrderStatusRequest;
import com.task.ecommerce.entity.*;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.event.OrderStatusChangedEvent;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.exception.ForbiddenException;
import com.task.ecommerce.exception.NotFoundException;
import com.task.ecommerce.repository.*;
import com.task.ecommerce.service.dto.OrderSummaryResponse;
import com.task.ecommerce.user.dto.OrderResponse;
import com.task.ecommerce.utils.OrderStatusValidator;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final OrderStatusValidator orderStatusValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse checkout(Integer userId) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty.");
        }

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException(
                            "Product no longer available: " + item.getProductId()));

            if (!product.isActive()) {
                throw new BadRequestException("Product is no longer active: " + product.getName());
            }

            if (product.getQuantity() < item.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for " + product.getName()
                                + ". Available: " + product.getQuantity());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(product.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            orderItemRepository.save(orderItem);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        cartItemRepository.deleteByUserId(userId);

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(totalAmount)
                .build();
    }

    @Transactional
    public void expireUnpaidOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PENDING, expiryTime);

        for (Order order : expiredOrders) {
            restoreStock(order.getId());
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancellationReason("Payment was not initiated before the reservation expired.");
            order.setCancelledAt(LocalDateTime.now());
            orderRepository.save(order);

            eventPublisher.publishEvent(new OrderStatusChangedEvent(
                    order.getId(), order.getUserId(), OrderStatus.PENDING, OrderStatus.CANCELLED));
        }
    }


    public PageResponse<OrderSummaryResponse> getAllOrders(int page, int size, OrderStatus status, User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Integer userId = user == null ? null : user.getId();
        Page<Order> orders = orderRepository.findOrders(status, userId, pageable);

        List<OrderSummaryResponse> items = orders.getContent()
                .stream()
                .map(order -> OrderSummaryResponse.builder()
                        .id(order.getId())
                        .userId(order.getUserId())
                        .status(order.getStatus().name())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<OrderSummaryResponse>builder()
                .items(items)
                .page(orders.getNumber())
                .size(orders.getSize())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .first(orders.isFirst())
                .last(orders.isLast())
                .build();
    }

    public OrderDetailResponse getOrderDetails(Integer orderId, User user) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (user != null){
            if(!order.getUserId().equals(user.getId())){
                throw new ForbiddenException("It's not your order.");
            }
        }

        User customer = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderDetailResponse.OrderItemInfo> items = orderItems.stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new NotFoundException("Product not found"));

                    BigDecimal subtotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return OrderDetailResponse.OrderItemInfo.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        return OrderDetailResponse.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .customer(OrderDetailResponse.CustomerInfo.builder()
                        .id(customer.getId())
                        .name(customer.getName())
                        .email(customer.getEmail())
                        .phone(customer.getPhone())
                        .build())
                .items(items)
                .build();
    }


    @Transactional
    public void updateOrderStatus(Integer orderId, OrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        orderStatusValidator.validateTransition(oldStatus, request.getStatus());

        order.setStatus(request.getStatus());
        orderRepository.save(order);

        eventPublisher.publishEvent(
                new OrderStatusChangedEvent(order.getId(), order.getUserId(), oldStatus, request.getStatus())
        );
    }

    private void restoreStock(Integer orderId) {
        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found."));
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
