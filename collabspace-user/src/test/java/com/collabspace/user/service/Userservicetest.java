package com.collabspace.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.collabspace.user.dto.UpdateProfileRequest;
import com.collabspace.user.dto.UserProfileResponse;
import com.collabspace.user.entity.UserProfile;
import com.collabspace.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;

    @InjectMocks
    private UserService userService;

    private UserProfile existingProfile;

    @BeforeEach
    void setUp() {
        existingProfile = new UserProfile();
        existingProfile.setId(1L);
        existingProfile.setEmail("yash@example.com");
        existingProfile.setName("Yash Chatse");
        existingProfile.setBio("Java Developer");
        existingProfile.setRole("MEMBER");
        existingProfile.setAvatarUrl(null);
    }

    // ── getProfile() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProfile() → returns existing profile when found")
    void getProfile_existingUser_returnsProfile() {
        when(userProfileRepository.findByEmail("yash@example.com"))
                .thenReturn(Optional.of(existingProfile));

        UserProfileResponse result = userService.getProfile("yash@example.com");

        assertThat(result.getEmail()).isEqualTo("yash@example.com");
        assertThat(result.getName()).isEqualTo("Yash Chatse");
        assertThat(result.getBio()).isEqualTo("Java Developer");
        assertThat(result.getRole()).isEqualTo("MEMBER");
        // Repository should NOT save — profile already exists
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("getProfile() → auto-creates profile when user not found (new login)")
    void getProfile_newUser_createsDefaultProfile() {
        UserProfile defaultProfile = new UserProfile();
        defaultProfile.setId(2L);
        defaultProfile.setEmail("new@example.com");
        defaultProfile.setName("new");   // email prefix before @
        defaultProfile.setRole("MEMBER");

        when(userProfileRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(defaultProfile);

        UserProfileResponse result = userService.getProfile("new@example.com");

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getName()).isEqualTo("new");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    // ── updateProfile() ───────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile() → updates name and bio, saves and returns response")
    void updateProfile_validRequest_updatesFields() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName("Yash C");
        req.setBio("Backend Engineer");

        when(userProfileRepository.findByEmail("yash@example.com"))
                .thenReturn(Optional.of(existingProfile));
        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponse result = userService.updateProfile("yash@example.com", req);

        assertThat(result.getName()).isEqualTo("Yash C");
        assertThat(result.getBio()).isEqualTo("Backend Engineer");
        verify(userProfileRepository).save(existingProfile);
    }

    @Test
    @DisplayName("updateProfile() → only updates non-null fields (partial update)")
    void updateProfile_partialRequest_onlyUpdatesNonNullFields() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName("Yash C");
        // bio is null — should not overwrite existing bio

        when(userProfileRepository.findByEmail("yash@example.com"))
                .thenReturn(Optional.of(existingProfile));
        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponse result = userService.updateProfile("yash@example.com", req);

        assertThat(result.getName()).isEqualTo("Yash C");
        assertThat(result.getBio()).isEqualTo("Java Developer"); // unchanged
    }

    // ── uploadAvatar() ────────────────────────────────────────────────────────

    @Test
    @DisplayName("uploadAvatar() → uploads to Cloudinary and saves secure_url to profile")
    void uploadAvatar_validFile_savesCloudinaryUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes());

        String expectedUrl = "https://res.cloudinary.com/demo/image/upload/collabspace/avatars/avatar.jpg";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", expectedUrl));
        when(userProfileRepository.findByEmail("yash@example.com"))
                .thenReturn(Optional.of(existingProfile));
        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponse result = userService.uploadAvatar("yash@example.com", file);

        assertThat(result.getAvatarUrl()).isEqualTo(expectedUrl);
        assertThat(existingProfile.getAvatarUrl()).isEqualTo(expectedUrl);
        verify(userProfileRepository).save(existingProfile);
    }

    // ── searchUsers() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("searchUsers() → returns list of profiles matching email fragment")
    void searchUsers_matchingFragment_returnsList() {
        when(userProfileRepository.findByEmailContaining("yash"))
                .thenReturn(List.of(existingProfile));

        List<UserProfileResponse> results = userService.searchUsers("yash");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo("yash@example.com");
    }

    @Test
    @DisplayName("searchUsers() → returns empty list when no email matches")
    void searchUsers_noMatch_returnsEmptyList() {
        when(userProfileRepository.findByEmailContaining("nobody"))
                .thenReturn(List.of());

        List<UserProfileResponse> results = userService.searchUsers("nobody");

        assertThat(results).isEmpty();
    }
}