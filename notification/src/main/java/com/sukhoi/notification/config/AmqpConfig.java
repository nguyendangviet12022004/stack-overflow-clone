package com.sukhoi.notification.config;

import com.sukhoi.notification.constant.AmqpExchange;
import com.sukhoi.notification.constant.AmqpQueue;
import com.sukhoi.notification.constant.AmqpRoutingKey;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange postTopicExchange() {
        return new TopicExchange(AmqpExchange.POST_TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue likeNotificationQueue() {
        return new Queue(AmqpQueue.NOTIFICATION_LIKE_QUEUE, true);
    }

    @Bean
    public Queue commentNotificationQueue() {
        return new Queue(AmqpQueue.NOTIFICATION_COMMENT_QUEUE, true);
    }

    @Bean
    public Binding likeNotificationBinding(Queue likeNotificationQueue, TopicExchange postTopicExchange) {
        return BindingBuilder
                .bind(likeNotificationQueue)
                .to(postTopicExchange)
                .with(AmqpRoutingKey.NOTIFICATION_LIKE_ROUTING_KEY);
    }

    @Bean
    public Binding commentNotificationBinding(Queue commentNotificationQueue, TopicExchange postTopicExchange) {
        return BindingBuilder
                .bind(commentNotificationQueue)
                .to(postTopicExchange)
                .with(AmqpRoutingKey.NOTIFICATION_COMMENT_ROUTING_KEY);
    }
}
