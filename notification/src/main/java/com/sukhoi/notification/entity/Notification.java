package com.sukhoi.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private Integer recipientId;
    private Integer senderId;
    private String type;
    private Long postId;
    private String message;
    private boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;
}
