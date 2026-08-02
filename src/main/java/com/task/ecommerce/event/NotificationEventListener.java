package com.task.ecommerce.event;

import com.task.ecommerce.entity.enums.NotificationType;
import com.task.ecommerce.notification.EmailService;
import com.task.ecommerce.notification.NotificationService;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.utils.EmailTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {

        log.info(
                "Received OrderStatusChangedEvent: orderId={}, userId={}, newStatus={}",
                event.getOrderId(),
                event.getUserId(),
                event.getNewStatus()
        );

        try {
            notificationService.createNotification(
                    event.getUserId(),
                    "Order Status Updated",
                    "Your order #" + event.getOrderId() + " status changed to " + event.getNewStatus(),
                    NotificationType.ORDER_STATUS_CHANGED
            );

            log.info(
                    "Notification created successfully for userId={}, orderId={}",
                    event.getUserId(),
                    event.getOrderId()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to create notification for userId={}, orderId={}",
                    event.getUserId(),
                    event.getOrderId(),
                    ex
            );

            throw ex;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        if (event.isSuccess()) {
            notificationService.createNotification(
                    event.getUserId(), "Payment Successful",
                    "Your payment of " + event.getAmount() + " EGP for order #" + event.getOrderId() + " was successful.",
                    NotificationType.PAYMENT_SUCCESS
            );
        } else {
            notificationService.createNotification(
                    event.getUserId(), "Payment Failed",
                    "Your payment for order #" + event.getOrderId() + " failed. Please try again.",
                    NotificationType.PAYMENT_FAILED
            );
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAdminAccountCreated(AdminAccountCreatedEvent event) {
        notificationService.createNotification(
                event.getAdminUserId(), "Account Created",
                "Your admin account has been created. Check your email for login credentials.",
                NotificationType.ADMIN_ACCOUNT_CREATED
        );
    }


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChangedSendEmail(OrderStatusChangedEvent event) {
        userRepository.findById(event.getUserId()).ifPresent(user -> {
            String html = EmailTemplates.orderStatusChanged(user.getName(), event.getOrderId(), event.getNewStatus().name());
            emailService.sendSimpleEmail(user.getEmail(), "Order #" + event.getOrderId() + " Update", html);
        });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentProcessedSendEmail(PaymentProcessedEvent event) {
        if (event.isSuccess()) {
            return;
        }

        userRepository.findById(event.getUserId()).ifPresent(user -> {
            String html = EmailTemplates.paymentFailed(user.getName(), event.getOrderId());
            emailService.sendSimpleEmail(user.getEmail(), "Payment Failed", html);
        });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAdminAccountCreatedSendEmail(AdminAccountCreatedEvent event) {
        String html = EmailTemplates.adminAccountCreated(event.getEmail(), event.getTemporaryPassword());
        emailService.sendSimpleEmail(event.getEmail(), "Your Admin Account", html);
    }
}
