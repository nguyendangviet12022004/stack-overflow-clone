package com.sukhoi.user.controller;

import com.sukhoi.user.message.AmqpMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private  final AmqpMessageSender messageSender;

    @GetMapping
    public String test() {
        messageSender.sendActivateMail(1, "viet.ngdang.dev@gmail.com", "123456");
        return "Test endpoint is working";
    }
}
