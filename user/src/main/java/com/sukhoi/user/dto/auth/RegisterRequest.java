package com.sukhoi.user.dto.auth;

public record RegisterRequest(
        String email, String password
) {
}
