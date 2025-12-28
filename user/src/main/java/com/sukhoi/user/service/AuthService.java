package com.sukhoi.user.service;

import com.sukhoi.user.dto.auth.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
