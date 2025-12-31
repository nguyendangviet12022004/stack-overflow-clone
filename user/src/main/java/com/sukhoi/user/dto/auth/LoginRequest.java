package com.sukhoi.user.dto.auth;

public record LoginRequest(
        String email, String password
) {
}
