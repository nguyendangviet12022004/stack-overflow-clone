package com.sukhoi.user.config;

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

}
