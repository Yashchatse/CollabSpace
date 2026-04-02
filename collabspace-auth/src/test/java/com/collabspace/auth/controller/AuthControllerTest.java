package com.collabspace.auth.controller;

import com.collabspace.auth.dto.AuthResponse;
import com.collabspace.auth.dto.LoginRequest;
import com.collabspace.auth.dto.RegisterRequest;
import com.collabspace.auth.security.JwtFilter;
import com.collabspace.auth.security.JwtUtil;
import com.collabspace.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean JwtFilter jwtFilter;
    @MockBean JwtUtil jwtUtil;

    private final AuthResponse fakeResponse =
            new AuthResponse("mock.jwt.token", "yash@example.com", "Yash Chatse", "MEMBER");

    // ── POST /api/auth/register ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register → 200 with AuthResponse on valid input")
    void register_validInput_returns200WithAuthResponse() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Yash Chatse");
        req.setEmail("yash@example.com");
        req.setPassword("password123");

        when(authService.register(any(RegisterRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())                                      // ← fix
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.email").value("yash@example.com"))
                .andExpect(jsonPath("$.name").value("Yash Chatse"))
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/register → 500 when email already registered")
    void register_duplicateEmail_returns500() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("yash@example.com");
        req.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already registered"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())                                      // ← fix
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is5xxServerError());
    }

    // ── POST /api/auth/login ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login → 200 with JWT token on valid credentials")
    void login_validCredentials_returns200WithToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("yash@example.com");
        req.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())                                      // ← fix
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login → 500 on wrong password")
    void login_wrongPassword_returns500() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("yash@example.com");
        req.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid password"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())                                      // ← fix
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is5xxServerError());
    }
}