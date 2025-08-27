package com.example.clubhub4.repository;

import com.example.clubhub4.entity.Notification;
import com.example.clubhub4.dto.NotificationView;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("""
        select new com.example.clubhub4.dto.NotificationView(
          n.id,
          n.message,
          coalesce(n.isRead, false),
          n.createdAt,
          a.id,
          c.id,
          c.name,
          a.status
        )
        from Notification n
        join n.application a
        join a.club c
        where n.recipient.id = :userId
        order by n.createdAt desc
        """)
    Page<NotificationView> findViewsByRecipientId(@Param("userId") UUID userId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isRead = true, n.readAt = current_timestamp where n.id = :id and n.recipient.id = :userId")
    int markOneRead(@Param("userId") UUID userId, @Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isRead = true, n.readAt = current_timestamp where n.recipient.id = :userId and (n.isRead = false or n.isRead is null)")
    int markAllRead(@Param("userId") UUID userId);

    long countByRecipient_IdAndIsReadFalse(UUID userId);
}