package com.sukhoi.mail.config;

import com.sukhoi.mail.constant.AmqpExchange;
import com.sukhoi.mail.constant.AmqpQueue;
import com.sukhoi.mail.constant.AmqpRoutingKey;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672",
    "spring.mail.host=localhost",
    "spring.mail.port=1025"
})
class AmqpConfigTest {

    @Autowired
    private AmqpConfig amqpConfig;

    @Autowired
    private TopicExchange userTopicExchange;

    @Autowired
    private Queue mailActiveAccountQueue;

    @Autowired
    private Binding mailActivateAccountBinding;

    @Test
    void contextLoads() {
        assertThat(amqpConfig).isNotNull();
    }

    @Test
    void jsonMessageConverter_ShouldBeCreated() {
        JacksonJsonMessageConverter converter = amqpConfig.jsonMessageConverter();

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(JacksonJsonMessageConverter.class);
    }

    @Test
    void userTopicExchange_ShouldBeConfiguredCorrectly() {
        assertThat(userTopicExchange).isNotNull();
        assertThat(userTopicExchange.getName()).isEqualTo(AmqpExchange.USER_TOPIC_EXCHANGE);
        assertThat(userTopicExchange.isDurable()).isTrue();
        assertThat(userTopicExchange.isAutoDelete()).isFalse();
        assertThat(userTopicExchange.getType()).isEqualTo("topic");
    }

    @Test
    void userTopicExchange_ShouldHaveCorrectName() {
        TopicExchange exchange = amqpConfig.userTopicExchange();

        assertThat(exchange.getName()).isEqualTo("x.user.topic");
    }

    @Test
    void mailActiveAccountQueue_ShouldBeConfiguredCorrectly() {
        assertThat(mailActiveAccountQueue).isNotNull();
        assertThat(mailActiveAccountQueue.getName()).isEqualTo(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE);
        assertThat(mailActiveAccountQueue.isDurable()).isTrue();
    }

    @Test
    void mailActiveAccountQueue_ShouldHaveCorrectName() {
        Queue queue = amqpConfig.mailActiveAccountQueue();

        assertThat(queue.getName()).isEqualTo("q.mail.active.account");
    }

    @Test
    void mailActiveAccountQueue_ShouldNotBeAutoDelete() {
        assertThat(mailActiveAccountQueue.isAutoDelete()).isFalse();
    }

    @Test
    void mailActivateAccountBinding_ShouldBeConfiguredCorrectly() {
        assertThat(mailActivateAccountBinding).isNotNull();
        assertThat(mailActivateAccountBinding.getDestination()).isEqualTo(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE);
        assertThat(mailActivateAccountBinding.getExchange()).isEqualTo(AmqpExchange.USER_TOPIC_EXCHANGE);
        assertThat(mailActivateAccountBinding.getRoutingKey()).isEqualTo(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY);
    }

    @Test
    void mailActivateAccountBinding_ShouldHaveCorrectRoutingKey() {
        Binding binding = amqpConfig.mailActivateAccountBinding(mailActiveAccountQueue, userTopicExchange);

        assertThat(binding.getRoutingKey()).isEqualTo("r.mail.active.account");
    }

    @Test
    void mailActivateAccountBinding_ShouldBeQueueBinding() {
        assertThat(mailActivateAccountBinding.getDestinationType()).isEqualTo(Binding.DestinationType.QUEUE);
    }

    @Test
    void allBeans_ShouldBeAutowiredCorrectly() {
        assertAll(
            () -> assertThat(amqpConfig).isNotNull(),
            () -> assertThat(userTopicExchange).isNotNull(),
            () -> assertThat(mailActiveAccountQueue).isNotNull(),
            () -> assertThat(mailActivateAccountBinding).isNotNull()
        );
    }

    @Test
    void topicExchange_ShouldSupportMultipleRoutingPatterns() {
        // Topic exchanges should support wildcard routing
        assertThat(userTopicExchange.getType()).isEqualTo("topic");
    }
}