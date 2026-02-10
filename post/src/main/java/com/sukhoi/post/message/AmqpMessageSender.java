package com.sukhoi.post.message;

import com.sukhoi.post.constant.AmqpExchange;
import com.sukhoi.post.dto.message.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmqpMessageSender {
    private final AmqpTemplate amqpTemplate;

    public void sendNotification(NotificationRequest request, String routingKey) {
        amqpTemplate.convertAndSend(AmqpExchange.POST_TOPIC_EXCHANGE, routingKey, request);
    }
}
