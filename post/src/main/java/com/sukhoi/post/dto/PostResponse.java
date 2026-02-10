package com.sukhoi.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PostResponse(
        Long id,
        String title,
        String content,
        Integer userId,
        LocalDateTime createdAt,
        Set<TagResponse> tags,
        List<CommentResponse> comments,
        long favoriteCount,
        boolean isFavorited) {
}
