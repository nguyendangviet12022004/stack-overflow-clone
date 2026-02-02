package com.sukhoi.mail.service;

public interface MailService {
    void sendActivationEmail(String email, int userId, String activationCode);
}
