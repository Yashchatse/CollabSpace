package com.collabspace.user.controller;

import com.collabspace.user.dto.UpdateProfileRequest;
import com.collabspace.user.dto.UserProfileResponse;
import com.collabspace.user.security.JwtFilter;
import com.collabspace.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserService userService;
    @MockBean JwtFilter jwtFilter;

    private UserProfileResponse fakeProfile() {
        return new UserProfileResponse(
                1L, "yash@example.com", "Yash Chatse",
                null, "Java Developer", "MEMBER");
    }

    // ── GET /api/users/me ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users/me → 200 with user profile")
    @WithMockUser(username = "yash@example.com", roles = "MEMBER")
    void getMyProfile_authenticated_returnsProfile() throws Exception {
        when(userService.getProfile("yash@example.com")).thenReturn(fakeProfile());

        mockMvc.perform(get("/api/users/me"))                      // GET — no csrf needed
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // ← fix
                .andExpect(jsonPath("$.email").value("yash@example.com"))
                .andExpect(jsonPath("$.name").value("Yash Chatse"))
                .andExpect(jsonPath("$.bio").value("Java Developer"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    // ── PUT /api/users/me ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/users/me → 200 with updated profile")
    @WithMockUser(username = "yash@example.com", roles = "MEMBER")
    void updateProfile_validInput_returnsUpdatedProfile() throws Exception {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName("Yash C");
        req.setBio("Backend Engineer");

        UserProfileResponse updated = new UserProfileResponse(
                1L, "yash@example.com", "Yash C",
                null, "Backend Engineer", "MEMBER");

        when(userService.updateProfile(eq("yash@example.com"), any(UpdateProfileRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/users/me")
                .with(csrf())                                      // ← fix: PUT needs csrf
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yash C"))
                .andExpect(jsonPath("$.bio").value("Backend Engineer"));
    }

    // ── POST /api/users/me/avatar ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/users/me/avatar → 200 with avatarUrl set")
    @WithMockUser(username = "yash@example.com", roles = "MEMBER")
    void uploadAvatar_validFile_returnsProfileWithAvatarUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake-bytes".getBytes());

        UserProfileResponse withAvatar = new UserProfileResponse(
                1L, "yash@example.com", "Yash Chatse",
                "https://res.cloudinary.com/demo/image/upload/avatar.jpg",
                "Java Developer", "MEMBER");

        when(userService.uploadAvatar(eq("yash@example.com"), any()))
                .thenReturn(withAvatar);

        mockMvc.perform(multipart("/api/users/me/avatar")
                .file(file)
                .with(csrf()))                                     // ← fix: POST needs csrf
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl")
                        .value("https://res.cloudinary.com/demo/image/upload/avatar.jpg"));
    }

    // ── GET /api/users/search ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users/search?email=yash → 200 with matching users")
    @WithMockUser(username = "yash@example.com", roles = "MEMBER")
    void searchUsers_validParam_returnsList() throws Exception {
        when(userService.searchUsers("yash")).thenReturn(List.of(fakeProfile()));

        mockMvc.perform(get("/api/users/search").param("email", "yash")) // GET — no csrf
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // ← fix
                .andExpect(jsonPath("$[0].email").value("yash@example.com"))
                .andExpect(jsonPath("$[0].name").value("Yash Chatse"));
    }
}