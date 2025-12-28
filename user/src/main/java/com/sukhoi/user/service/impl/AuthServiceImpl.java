package com.sukhoi.user.service.impl;

import com.sukhoi.user.dto.auth.RegisterRequest;
import com.sukhoi.user.repository.UserRepository;
import com.sukhoi.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    @Override
    public void register(RegisterRequest request) {

        // check if user with email already exists
        if (userRepository.existsByEmail((request.email()))){
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }

        var user = com.sukhoi.user.entity.User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .isActive(true)
                .build();

        userRepository.save(user);

        // todo send verification email
    }
}
