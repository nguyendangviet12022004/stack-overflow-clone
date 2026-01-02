package com.sukhoi.mail.message;

import com.sukhoi.mail.dto.message.ActivateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReceiver {
    @RabbitListener(queues = "q.mail.active.account")
    public void processOrder(ActivateAccountRequest request, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        System.out.print(request);
        System.out.println("Routing Key: " + routingKey);
    }
}
