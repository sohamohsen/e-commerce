package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Order;
import com.task.ecommerce.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
        SELECT o FROM Order o
        WHERE (:status IS NULL OR o.status = :status) AND (:userId IS NULL OR o.userId = :userId)
        """)
    Page<Order> findOrders(@Param("status") OrderStatus status, @Param("userId") Integer userId, Pageable pageable);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAt);

    @Query("SELECT o.id FROM Order o WHERE o.userId = :userId")
    List<Integer> findOrderIdsByUserId(@Param("userId") Integer userId);}
