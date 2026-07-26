package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.OrderDetailResponse;
import com.task.ecommerce.admin.dto.CancelOrderRequest;
import com.task.ecommerce.admin.dto.OrderStatusRequest;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.service.OrderService;
import com.task.ecommerce.payment.PaymentService;
import com.task.ecommerce.service.dto.OrderSummaryResponse;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.task.ecommerce.entity.User;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin-orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status
    ) {
        PageResponse<OrderSummaryResponse> orders = orderService.getAllOrders(page, size, status, null);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Orders fetched successfully.")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @PathVariable Integer orderId
    ) {
        OrderDetailResponse orders = orderService.getOrderDetails(orderId, null);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Order fetched successfully.")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody @Valid OrderStatusRequest request
    ) {
        orderService.updateOrderStatus(orderId, request);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Order updated successfully.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Integer orderId,
            @RequestBody @Valid CancelOrderRequest request,
            @AuthenticationPrincipal User admin
    ) {
        paymentService.cancelOrder(orderId, admin.getId(), request.getReason());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Order cancellation was requested successfully.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
