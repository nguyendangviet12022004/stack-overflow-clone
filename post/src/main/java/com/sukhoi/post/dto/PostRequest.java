package com.sukhoi.post.dto;

import java.util.Set;

public record PostRequest(String title, String content, Set<String> tags) {
}
