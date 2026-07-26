package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByOrderId(Integer orderId);
}