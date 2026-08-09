package com.task.ecommerce.repository;

import com.task.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);

    List<CartItem> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);

    @Query(value = """
    SELECT oi2.product_id
    FROM cart_item ci
    JOIN order_item oi1
        ON ci.product_id = oi1.product_id
    JOIN orders o1
        ON oi1.order_id = o1.id
    JOIN order_item oi2
        ON oi2.order_id = o1.id
    WHERE ci.user_id = :userId
      AND o1.user_id != :userId
      AND oi2.product_id NOT IN (
          SELECT ci2.product_id
          FROM cart_item ci2
          WHERE ci2.user_id = :userId
      )
    GROUP BY oi2.product_id
    ORDER BY COUNT(DISTINCT o1.user_id) DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Integer> findCollaborativeRecommendations(
            @Param("userId") Integer userId
    );

    @Query(value = """
    SELECT oi2.product_id
    FROM order_item oi1
    JOIN orders o ON o.id = oi1.order_id
    JOIN order_item oi2 ON oi2.order_id = oi1.order_id
    WHERE oi1.product_id = :productId
      AND o.user_id != :userId
      AND oi2.product_id != :productId
      AND oi2.product_id NOT IN (
          SELECT ci.product_id
          FROM cart_item ci
          WHERE ci.user_id = :userId
      )
    GROUP BY oi2.product_id
    ORDER BY COUNT(DISTINCT oi1.order_id) DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Integer> findCollaborativeRecommendationsForProduct(
            @Param("userId") Integer userId,
            @Param("productId") Integer productId
    );
}
