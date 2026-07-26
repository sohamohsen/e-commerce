package com.task.ecommerce.notification;

import com.task.ecommerce.entity.Notification;
import com.task.ecommerce.entity.enums.NotificationType;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.notification.dto.NotificationResponse;
import com.task.ecommerce.repository.NotificationRepository;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(Integer userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    public PageResponse<NotificationResponse> getNotifications(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);

        List<NotificationResponse> items = notifications.getContent()
                .stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .type(n.getType().name())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<NotificationResponse>builder()
                .items(items)
                .page(notifications.getNumber())
                .size(notifications.getSize())
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .first(notifications.isFirst())
                .last(notifications.isLast())
                .build();
    }

    public void markAsRead(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException("Notification not found."));

        if (!notification.getUserId().equals(userId)) {
            throw new BadRequestException("Notification not found.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}