package com.sukhoi.mail.config;

import com.sukhoi.mail.constant.AmqpQueue;
import com.sukhoi.mail.constant.AmqpExchange;
import com.sukhoi.mail.constant.AmqpRoutingKey;
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
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        return jsonConverter;
    }
    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange(AmqpExchange.USER_TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue mailActiveAccountQueue() {
        return new Queue(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE, true);
    }

    @Bean
    Binding mailActivateAccountBinding(Queue mailActiveAccountQueue, TopicExchange userTopicExchange) {
        return BindingBuilder
                .bind(mailActiveAccountQueue)
                .to(userTopicExchange)
                .with(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY);
    }
}
