package com.sukhoi.mail.integration;

import com.sukhoi.mail.dto.message.ActivateAccountRequest;
import com.sukhoi.mail.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672",
    "spring.mail.host=localhost",
    "spring.mail.port=1025",
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
class MailServiceIntegrationTest {

    @Autowired
    private MailService mailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void contextLoads() {
        assertThat(mailService).isNotNull();
    }

    @Test
    void sendActivationEmail_ShouldWorkEndToEnd() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String email = "integration@test.com";
        int userId = 999;
        String activationCode = "INTEGRATION123";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void mailService_ShouldBeInjectable() {
        assertThat(mailService).isNotNull();
    }

    @Test
    void sendActivationEmail_ShouldHandleMultipleCallsInIntegrationContext() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        mailService.sendActivationEmail("user1@test.com", 1, "CODE1");
        mailService.sendActivationEmail("user2@test.com", 2, "CODE2");
        mailService.sendActivationEmail("user3@test.com", 3, "CODE3");

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void mailService_ShouldWorkWithDifferentEmailFormats() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        mailService.sendActivationEmail("simple@example.com", 1, "CODE");
        mailService.sendActivationEmail("with+plus@example.com", 2, "CODE");
        mailService.sendActivationEmail("with.dot@example.com", 3, "CODE");

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void mailService_ShouldHandleEdgeCaseUserIds() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        mailService.sendActivationEmail("test@test.com", 0, "CODE");
        mailService.sendActivationEmail("test@test.com", -1, "CODE");
        mailService.sendActivationEmail("test@test.com", Integer.MAX_VALUE, "CODE");

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void mailService_ShouldHandleVariousActivationCodeFormats() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        mailService.sendActivationEmail("test@test.com", 1, "SIMPLE");
        mailService.sendActivationEmail("test@test.com", 2, "With-Dashes");
        mailService.sendActivationEmail("test@test.com", 3, "With_Underscores");
        mailService.sendActivationEmail("test@test.com", 4, "123456");

        verify(mailSender, times(4)).send(any(SimpleMailMessage.class));
    }
}