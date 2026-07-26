package com.task.ecommerce.user;

import com.task.ecommerce.admin.dto.OrderDetailResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.OrderStatus;
import com.task.ecommerce.service.OrderService;
import com.task.ecommerce.service.dto.OrderSummaryResponse;
import com.task.ecommerce.user.dto.OrderResponse;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal User user) {
        OrderResponse order = orderService.checkout(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Order created successfully.")
                .data(order)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @AuthenticationPrincipal User user
    ) {
        PageResponse<OrderSummaryResponse> orders = orderService.getAllOrders(page, size, status, user);

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
            @PathVariable Integer orderId,
            @AuthenticationPrincipal User user
    ) {
        OrderDetailResponse orders = orderService.getOrderDetails(orderId, user);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Order fetched successfully.")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }
}
