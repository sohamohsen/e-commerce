package com.task.ecommerce.repository;

import com.task.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);

    List<OrderItem> findByOrderIdIn(List<Integer> orderIds);

    @Query(value = """
    SELECT oi2.product_id, COUNT(DISTINCT oi2.order_id) as frequency
    FROM order_item oi1
    JOIN orders o1 ON oi1.order_id = o1.id
    JOIN order_item oi2 ON oi1.order_id != oi2.order_id
    JOIN orders o2 ON oi2.order_id = o2.id
    WHERE oi1.product_id IN (
        SELECT DISTINCT oi.product_id
        FROM order_item oi
        JOIN orders o ON oi.order_id = o.id
        WHERE o.user_id = :userId
    )
    AND o2.user_id != :userId
    AND oi2.product_id NOT IN (
        SELECT DISTINCT oi.product_id
        FROM order_item oi
        JOIN orders o ON oi.order_id = o.id
        WHERE o.user_id = :userId
    )
    GROUP BY oi2.product_id
    ORDER BY frequency DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Integer> findCollaborativeRecommendations(@Param("userId") Integer userId);
}
