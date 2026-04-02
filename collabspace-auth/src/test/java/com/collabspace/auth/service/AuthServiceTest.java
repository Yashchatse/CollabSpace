package com.collabspace.auth.service;

import com.collabspace.auth.dto.AuthResponse;
import com.collabspace.auth.dto.LoginRequest;
import com.collabspace.auth.dto.RegisterRequest;
import com.collabspace.auth.entity.User;
import com.collabspace.auth.repository.UserRepository;
import com.collabspace.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Yash Chatse");
        existingUser.setEmail("yash@example.com");
        existingUser.setPassword("$2a$10$hashedpassword");
        existingUser.setRole(User.Role.MEMBER);
    }

    // ── register() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("register() → returns AuthResponse with token on success")
    void register_newEmail_returnsAuthResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Yash Chatse");
        req.setEmail("yash@example.com");
        req.setPassword("password123");

        when(userRepository.existsByEmail("yash@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(jwtUtil.generateToken("yash@example.com", "MEMBER")).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(req);

        assertThat(response.getAccessToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("yash@example.com");
        assertThat(response.getName()).isEqualTo("Yash Chatse");
        assertThat(response.getRole()).isEqualTo("MEMBER");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() → throws RuntimeException when email already registered")
    void register_duplicateEmail_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("yash@example.com");

        when(userRepository.existsByEmail("yash@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    // ── login() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login() → returns AuthResponse with JWT on valid credentials")
    void login_validCredentials_returnsAuthResponse() {
        LoginRequest req = new LoginRequest();
        req.setEmail("yash@example.com");
        req.setPassword("password123");

        when(userRepository.findByEmail("yash@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("yash@example.com", "MEMBER")).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(req);

        assertThat(response.getAccessToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("yash@example.com");
        assertThat(response.getRole()).isEqualTo("MEMBER");
        verify(jwtUtil).generateToken("yash@example.com", "MEMBER");
    }

    @Test
    @DisplayName("login() → throws RuntimeException when user not found")
    void login_unknownEmail_throwsUserNotFoundException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("ghost@example.com");
        req.setPassword("password123");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login() → throws RuntimeException on wrong password")
    void login_wrongPassword_throwsInvalidPasswordException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("yash@example.com");
        req.setPassword("wrongpassword");

        when(userRepository.findByEmail("yash@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid password");

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}