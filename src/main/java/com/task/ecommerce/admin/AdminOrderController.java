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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders (Admin)", description = "Admin management for viewing orders, updating statuses, and processing cancellations/refunds")
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Operation(summary = "Get all orders", description = "Fetch all customer orders across the platform with optional status filter.")
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

    @Operation(summary = "Get order details", description = "Fetch full details of any customer order by ID.")
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

    @Operation(summary = "Update order status", description = "Updates order status (e.g. PROCESSING, SHIPPED, DELIVERED).")
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

    @Operation(summary = "Cancel order & process refund", description = "Cancels an order and initiates a Paymob refund if applicable.")
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
