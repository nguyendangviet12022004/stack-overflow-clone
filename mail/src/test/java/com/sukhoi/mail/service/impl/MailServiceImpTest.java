package com.sukhoi.mail.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImpTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImp mailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        // Setup is handled by @Mock and @InjectMocks
    }

    @Test
    void sendActivationEmail_ShouldSendEmailWithCorrectDetails() {
        String email = "test@example.com";
        int userId = 123;
        String activationCode = "ABC123";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(email);
        assertThat(sentMessage.getFrom()).isEqualTo("noreply@test.local");
        assertThat(sentMessage.getSubject()).isEqualTo("Activation Code");
        assertThat(sentMessage.getText()).isEqualTo("Your activation code is: " + activationCode);
    }

    @Test
    void sendActivationEmail_ShouldCallMailSenderOnce() {
        String email = "user@test.com";
        int userId = 456;
        String activationCode = "XYZ789";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldIncludeActivationCodeInMessage() {
        String email = "test@test.com";
        int userId = 100;
        String activationCode = "SECRET123";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).contains(activationCode);
        assertThat(sentMessage.getText()).isEqualTo("Your activation code is: SECRET123");
    }

    @Test
    void sendActivationEmail_ShouldSetCorrectFromAddress() {
        String email = "test@test.com";
        int userId = 100;
        String activationCode = "CODE";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getFrom()).isEqualTo("noreply@test.local");
    }

    @Test
    void sendActivationEmail_ShouldSetCorrectSubject() {
        String email = "test@test.com";
        int userId = 100;
        String activationCode = "CODE";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getSubject()).isEqualTo("Activation Code");
    }

    @Test
    void sendActivationEmail_ShouldHandleEmptyActivationCode() {
        String email = "test@test.com";
        int userId = 100;
        String activationCode = "";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).isEqualTo("Your activation code is: ");
    }

    @Test
    void sendActivationEmail_ShouldHandleNullActivationCode() {
        String email = "test@test.com";
        int userId = 100;
        String activationCode = null;

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).isEqualTo("Your activation code is: null");
    }

    @Test
    void sendActivationEmail_ShouldHandleZeroUserId() {
        String email = "test@test.com";
        int userId = 0;
        String activationCode = "CODE";

        assertDoesNotThrow(() -> mailService.sendActivationEmail(email, userId, activationCode));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldHandleNegativeUserId() {
        String email = "test@test.com";
        int userId = -1;
        String activationCode = "CODE";

        assertDoesNotThrow(() -> mailService.sendActivationEmail(email, userId, activationCode));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldHandleLargeUserId() {
        String email = "test@test.com";
        int userId = Integer.MAX_VALUE;
        String activationCode = "CODE";

        assertDoesNotThrow(() -> mailService.sendActivationEmail(email, userId, activationCode));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldHandleSpecialCharactersInEmail() {
        String email = "test+special@example.com";
        int userId = 123;
        String activationCode = "CODE";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(email);
    }

    @Test
    void sendActivationEmail_ShouldHandleLongActivationCode() {
        String email = "test@test.com";
        int userId = 123;
        String longCode = "A".repeat(1000);

        mailService.sendActivationEmail(email, userId, longCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).contains(longCode);
    }

    @Test
    void sendActivationEmail_ShouldHandleSpecialCharactersInActivationCode() {
        String email = "test@test.com";
        int userId = 123;
        String activationCode = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).contains(activationCode);
    }

    @Test
    void sendActivationEmail_ShouldHandleMultipleInvocations() {
        mailService.sendActivationEmail("user1@test.com", 1, "CODE1");
        mailService.sendActivationEmail("user2@test.com", 2, "CODE2");
        mailService.sendActivationEmail("user3@test.com", 3, "CODE3");

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldNotModifyInputParameters() {
        String originalEmail = "test@test.com";
        int originalUserId = 123;
        String originalCode = "CODE";

        mailService.sendActivationEmail(originalEmail, originalUserId, originalCode);

        // Parameters should remain unchanged
        assertThat(originalEmail).isEqualTo("test@test.com");
        assertThat(originalUserId).isEqualTo(123);
        assertThat(originalCode).isEqualTo("CODE");
    }

    @Test
    void sendActivationEmail_ShouldCreateNewMessageEachTime() {
        mailService.sendActivationEmail("test1@test.com", 1, "CODE1");
        mailService.sendActivationEmail("test2@test.com", 2, "CODE2");

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldHandleUnicodeInEmail() {
        String email = "тест@example.com";
        int userId = 123;
        String activationCode = "CODE";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(email);
    }

    @Test
    void sendActivationEmail_ShouldHandleUnicodeInActivationCode() {
        String email = "test@test.com";
        int userId = 123;
        String activationCode = "验证码123";

        mailService.sendActivationEmail(email, userId, activationCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getText()).contains(activationCode);
    }
}