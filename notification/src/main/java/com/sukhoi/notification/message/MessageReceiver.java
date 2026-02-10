package com.sukhoi.notification.message;

import com.sukhoi.notification.constant.AmqpQueue;
import com.sukhoi.notification.dto.message.NotificationRequest;
import com.sukhoi.notification.entity.Notification;
import com.sukhoi.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = AmqpQueue.NOTIFICATION_LIKE_QUEUE)
    public void receiveLikeNotification(NotificationRequest request) {
        saveAndPushNotification(request);
    }

    @RabbitListener(queues = AmqpQueue.NOTIFICATION_COMMENT_QUEUE)
    public void receiveCommentNotification(NotificationRequest request) {
        saveAndPushNotification(request);
    }

    private void saveAndPushNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .senderId(request.getSenderId())
                .type(request.getType())
                .postId(request.getPostId())
                .message(request.getMessage())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                request.getRecipientId().toString(),
                "/queue/notifications",
                saved);
    }
}
