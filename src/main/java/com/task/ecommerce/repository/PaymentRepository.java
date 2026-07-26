package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Payment;
import com.task.ecommerce.entity.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByPaymobOrderId(String paymobOrderId);

    Optional<Payment> findFirstByOrderIdOrderByCreatedAtDesc(Integer orderId);

    List<Payment> findByStatusAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime createdAt
    );
}
