package com.sukhoi.notification.repository;

import com.sukhoi.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId);

    List<Notification> findByRecipientIdAndIsReadFalse(Integer recipientId);
}
