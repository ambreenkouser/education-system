package com.edumanage.authservice;

import com.edumanage.authservice.dto.LoginRequest;
import com.edumanage.authservice.dto.LoginResponse;
import com.edumanage.authservice.dto.RegisterRequest;
import com.edumanage.authservice.event.UserCreatedEvent;
import com.edumanage.authservice.model.Role;
import com.edumanage.authservice.model.User;
import com.edumanage.authservice.repository.RefreshTokenRepository;
import com.edumanage.authservice.repository.UserRepository;
import com.edumanage.authservice.service.AuthService;
import com.edumanage.authservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock JwtService jwtService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @InjectMocks AuthService authService;

    @Test
    void register_shouldCreateUserAndReturnTokens() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("teacher@school.com");
        req.setPassword("password123");
        req.setRole(Role.TEACHER);

        User saved = User.builder()
                .id(UUID.randomUUID())
                .email(req.getEmail())
                .password("encoded")
                .role(Role.TEACHER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshTokenValue()).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any())).thenReturn(null);

        LoginResponse response = authService.register(req);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRole()).isEqualTo("TEACHER");
        verify(kafkaTemplate).send(eq("user.created"), anyString(), any(UserCreatedEvent.class));
    }

    @Test
    void login_withWrongPassword_shouldThrowBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@school.com");
        req.setPassword("wrong");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(req.getEmail())
                .password("encoded")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
