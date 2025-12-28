package com.sukhoi.user.service.impl;

import com.sukhoi.user.dto.auth.LoginRequest;
import com.sukhoi.user.dto.auth.RegisterRequest;
import com.sukhoi.user.dto.auth.TokenResponse;
import com.sukhoi.user.entity.User;
import com.sukhoi.user.repository.UserRepository;
import com.sukhoi.user.service.AuthService;
import com.sukhoi.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication auth =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));


        User user = (User) auth.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user.getId());

        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new TokenResponse(accessToken, refreshToken);
    }
}
