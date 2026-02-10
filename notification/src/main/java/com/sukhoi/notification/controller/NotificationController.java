package com.sukhoi.notification.controller;

import com.sukhoi.notification.entity.Notification;
import com.sukhoi.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getRecipientId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        List<Notification> unread = notificationRepository.findByRecipientIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok().build();
    }
}
