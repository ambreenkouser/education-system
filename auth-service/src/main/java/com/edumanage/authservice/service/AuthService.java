package com.edumanage.authservice.service;

import com.edumanage.authservice.dto.LoginRequest;
import com.edumanage.authservice.dto.LoginResponse;
import com.edumanage.authservice.dto.RegisterRequest;
import com.edumanage.authservice.event.UserCreatedEvent;
import com.edumanage.authservice.model.RefreshToken;
import com.edumanage.authservice.model.User;
import com.edumanage.authservice.repository.RefreshTokenRepository;
import com.edumanage.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final LoginAttemptService loginAttemptService;

    private static final String USER_CREATED_TOPIC = "user.created";
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            // Generic message — do not reveal whether email is registered
            throw new IllegalArgumentException("Registration failed. Please check your details and try again.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        kafkaTemplate.send(USER_CREATED_TOPIC, savedUser.getId().toString(),
                UserCreatedEvent.builder()
                        .userId(savedUser.getId())
                        .email(savedUser.getEmail())
                        .role(savedUser.getRole().name())
                        .createdAt(savedUser.getCreatedAt())
                        .build());

        return buildLoginResponse(savedUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isBlocked(email)) {
            throw new BadCredentialsException("Account temporarily locked due to too many failed attempts. Try again later.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    loginAttemptService.recordFailure(email);
                    return new BadCredentialsException("Invalid email or password");
                });

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(email);
            throw new BadCredentialsException("Invalid email or password");
        }

        loginAttemptService.clearAttempts(email);
        return buildLoginResponse(user);
    }

    @Transactional
    public void logout(String userId, String accessToken) {
        refreshTokenRepository.deleteAllByUserId(java.util.UUID.fromString(userId));
        // Blacklist the access token in Redis so the gateway rejects it immediately
        if (accessToken != null && !accessToken.isBlank()) {
            jwtService.revokeToken(accessToken);
        }
    }

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshTokenValue = jwtService.generateRefreshTokenValue();

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
                .build());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .role(user.getRole().name())
                .userId(user.getId().toString())
                .build();
    }
}
