package com.sukhoi.user.message;

import com.sukhoi.user.constant.AmqpExchange;
import com.sukhoi.user.constant.AmqpRoutingKey;
import com.sukhoi.user.dto.message.ActivateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmqpMessageSender {

    private final AmqpTemplate amqpTemplate;

    public void sendActivateMail(int userId, String email, String activationCode) {
        var request = ActivateAccountRequest.builder()
                .userId(String.valueOf(userId))
                .email(email)
                .activationCode(activationCode)
                .build();
        amqpTemplate.convertAndSend(AmqpExchange.USER_TOPIC_EXCHANGE, AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY, request);
    }
}
