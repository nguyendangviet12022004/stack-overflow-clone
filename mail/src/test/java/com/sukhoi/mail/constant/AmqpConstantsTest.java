package com.sukhoi.mail.constant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AmqpConstantsTest {

    @Test
    void amqpExchange_ShouldHaveCorrectUserTopicExchangeName() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).isEqualTo("x.user.topic");
    }

    @Test
    void amqpExchange_ShouldNotBeNull() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).isNotNull();
    }

    @Test
    void amqpExchange_ShouldNotBeEmpty() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).isNotEmpty();
    }

    @Test
    void amqpExchange_ShouldFollowNamingConvention() {
        // Exchange names should start with 'x.'
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).startsWith("x.");
    }

    @Test
    void amqpQueue_ShouldHaveCorrectMailActiveAccountQueueName() {
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).isEqualTo("q.mail.active.account");
    }

    @Test
    void amqpQueue_ShouldNotBeNull() {
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).isNotNull();
    }

    @Test
    void amqpQueue_ShouldNotBeEmpty() {
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).isNotEmpty();
    }

    @Test
    void amqpQueue_ShouldFollowNamingConvention() {
        // Queue names should start with 'q.'
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).startsWith("q.");
    }

    @Test
    void amqpQueue_ShouldContainMailPrefix() {
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).contains("mail");
    }

    @Test
    void amqpRoutingKey_ShouldHaveCorrectMailActiveAccountRoutingKey() {
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).isEqualTo("r.mail.active.account");
    }

    @Test
    void amqpRoutingKey_ShouldNotBeNull() {
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).isNotNull();
    }

    @Test
    void amqpRoutingKey_ShouldNotBeEmpty() {
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).isNotEmpty();
    }

    @Test
    void amqpRoutingKey_ShouldFollowNamingConvention() {
        // Routing keys should start with 'r.'
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).startsWith("r.");
    }

    @Test
    void amqpRoutingKey_ShouldContainMailPrefix() {
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).contains("mail");
    }

    @Test
    void constants_ShouldBeConsistentAcrossExchangeQueueAndRoutingKey() {
        // All three should contain "active.account"
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).contains("active.account");
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).contains("active.account");
    }

    @Test
    void constants_ShouldUseDotSeparation() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).contains(".");
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).contains(".");
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).contains(".");
    }

    @Test
    void constants_ShouldNotContainWhitespace() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).doesNotContain(" ");
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).doesNotContain(" ");
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).doesNotContain(" ");
    }

    @Test
    void constants_ShouldBeLowerCase() {
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE).isLowerCase();
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE).isLowerCase();
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY).isLowerCase();
    }

    @Test
    void constants_ShouldHaveReasonableLength() {
        // Constants should not be too long
        assertThat(AmqpExchange.USER_TOPIC_EXCHANGE.length()).isLessThan(100);
        assertThat(AmqpQueue.MAIL_ACTIVE_ACCOUNT_QUEUE.length()).isLessThan(100);
        assertThat(AmqpRoutingKey.MAIL_ACTIVE_ACCOUNT_ROUTING_KEY.length()).isLessThan(100);
    }
}