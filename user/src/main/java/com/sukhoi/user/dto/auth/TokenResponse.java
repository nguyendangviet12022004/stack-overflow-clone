package com.sukhoi.user.dto.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
