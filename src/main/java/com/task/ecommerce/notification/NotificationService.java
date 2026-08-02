package com.task.ecommerce.notification;

import com.task.ecommerce.entity.Notification;
import com.task.ecommerce.entity.enums.NotificationType;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.notification.dto.NotificationCount;
import com.task.ecommerce.notification.dto.NotificationResponse;
import com.task.ecommerce.repository.NotificationRepository;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNotification(Integer userId,
                                   String title,
                                   String message,
                                   NotificationType type) {

        log.info("Entering createNotification");

        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        log.info("Before save");

        Notification saved = notificationRepository.saveAndFlush(notification);

        log.info("After save id={}", saved.getId());
    }

    @Transactional
    public PageResponse<NotificationResponse> getNotifications(Integer userId, Boolean read, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Notification> notifications;

        // Handle the read parameter properly
        if (read != null) {
            // If read is true, get only read notifications
            // If read is false, get only unread notifications
            notifications = notificationRepository.findByUserIdAndIsRead(userId, read, pageable);
        } else {
            // If read is null, get all notifications (both read and unread)
            notifications = notificationRepository.findByUserId(userId, pageable);
        }

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

    @Transactional
    public void markAsRead(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException("Notification not found."));

        if (!notification.getUserId().equals(userId)) {
            throw new BadRequestException("Notification not found.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public NotificationCount getNotificationsUnreadedCount(Integer userId) {
        return NotificationCount.builder()
                .count(notificationRepository.countByUserIdAndIsReadFalse(userId))
                .build();
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        // Use the efficient update query (recommended)
        int updatedCount = notificationRepository.updateAllAsReadByUserId(userId);
        log.info("Marked {} notifications as read for user {}", updatedCount, userId);

        // Alternative: Find all unread notifications and update them
        // List<Notification> unreadNotifications = notificationRepository
        //         .findByUserIdAndIsReadFalse(userId);
        //
        // if (!unreadNotifications.isEmpty()) {
        //     unreadNotifications.forEach(notification -> notification.setRead(true));
        //     notificationRepository.saveAll(unreadNotifications);
        // }
    }
}