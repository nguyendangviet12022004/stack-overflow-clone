package com.sukhoi.user.service;

import com.sukhoi.user.dto.auth.LoginRequest;
import com.sukhoi.user.dto.auth.RegisterRequest;
import com.sukhoi.user.dto.auth.TokenResponse;

public interface AuthService {
    void register(RegisterRequest request);

    TokenResponse login(LoginRequest request);
}
