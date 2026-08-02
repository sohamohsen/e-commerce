package com.task.ecommerce.notification;

import com.task.ecommerce.entity.User;
import com.task.ecommerce.notification.dto.NotificationCount;
import com.task.ecommerce.notification.dto.NotificationResponse;
import com.task.ecommerce.utils.PageResponse;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean read,
            @AuthenticationPrincipal User user
    ) {
        PageResponse<NotificationResponse> notifications =
                notificationService.getNotifications(user.getId(), false, page, size);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Notifications fetched successfully.")
                .data(notifications)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Integer notificationId,
            @AuthenticationPrincipal User user
    ) {
        notificationService.markAsRead(user.getId(), notificationId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Notification marked as read.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markAsAllReaded(
            @AuthenticationPrincipal User user
    ) {
        notificationService.markAllAsRead(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("All notifications marked as read.")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getNotificationsUnreadedCount(
            @AuthenticationPrincipal User user
    ) {
        NotificationCount notifications =
                notificationService.getNotificationsUnreadedCount(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Notifications fetched successfully.")
                .data(notifications)
                .build();

        return ResponseEntity.ok(response);
    }
}