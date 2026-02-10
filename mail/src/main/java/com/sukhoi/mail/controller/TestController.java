package com.sukhoi.mail.controller;

import com.sukhoi.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test-mail")
public class TestController {
    private final MailService mailService;

    @GetMapping("")
    public String test() {
        mailService.sendActivationEmail("viet@gmail", 2, "code");
        return "Mail Service is working!";
    }
}
