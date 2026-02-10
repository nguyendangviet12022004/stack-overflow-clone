package com.sukhoi.post.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Integer userId,
        LocalDateTime createdAt,
        long replyCount) {
}
