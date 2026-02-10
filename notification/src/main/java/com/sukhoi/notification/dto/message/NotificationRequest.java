package com.sukhoi.notification.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Integer recipientId;
    private Integer senderId;
    private String type;
    private Long postId;
    private String message;
}
