package com.sukhoi.notification.controller;

import com.sukhoi.notification.entity.Notification;
import com.sukhoi.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;
    @GetMapping
    public String test(Authentication authentication) {
        this.messagingTemplate.convertAndSendToUser(authentication.getPrincipal().toString(), "/queue/notifications",
                MessageBuilder.withPayload("Test notification").build());
        return "Notification Service is up and running!";
    }

    @GetMapping("notification")
    public ResponseEntity<?> getNotification() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping("notifications")
    public ResponseEntity<?> createNotification(@RequestBody  Notification notification) {
        notificationRepository.save(notification);
        return ResponseEntity.ok("Notification created");
    }
}
