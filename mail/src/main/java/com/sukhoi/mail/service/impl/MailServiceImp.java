package com.sukhoi.mail.service.impl;

import com.sukhoi.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendActivationEmail(String email, int userId, String activationCode) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("noreply@test.local");
        message.setSubject("Activation Code");
        message.setText("Your activation code is: " + activationCode);

        mailSender.send(message);
    }
}
