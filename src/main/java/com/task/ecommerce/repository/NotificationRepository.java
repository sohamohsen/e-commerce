package com.task.ecommerce.repository;

import com.task.ecommerce.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByUserIdAndIsRead(Integer userId, Boolean isRead, Pageable pageable);

    Page<Notification> findByUserId(Integer userId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalse(Integer userId);

    Page<Notification> findByUserIdAndIsReadFalse(Integer userId, Pageable pageable);

    int countByUserIdAndIsReadFalse(Integer userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int updateAllAsReadByUserId(@Param("userId") Integer userId);
}