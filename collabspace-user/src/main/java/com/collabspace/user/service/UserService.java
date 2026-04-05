package com.collabspace.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.collabspace.user.dto.UpdateProfileRequest;
import com.collabspace.user.dto.UserProfileResponse;
import com.collabspace.user.entity.UserProfile;
import com.collabspace.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final Cloudinary cloudinary;

    public UserService(UserProfileRepository userProfileRepository,
                       Cloudinary cloudinary) {
        this.userProfileRepository = userProfileRepository;
        this.cloudinary = cloudinary;
    }

    public UserProfileResponse getProfile(String email) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseGet(() -> createDefaultProfile(email));
        return toResponse(profile);
    }

    public UserProfileResponse updateProfile(String email,
                                              UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseGet(() -> createDefaultProfile(email));

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getBio() != null) profile.setBio(request.getBio());

        return toResponse(userProfileRepository.save(profile));
    }

    public UserProfileResponse uploadAvatar(String email,
                                             MultipartFile file) throws IOException {
    	@SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "collabspace/avatars",
                        "public_id", email.replace("@", "_").replace(".", "_"),
                        "overwrite", true
                )
        );

        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseGet(() -> createDefaultProfile(email));
        profile.setAvatarUrl((String) uploadResult.get("secure_url"));

        return toResponse(userProfileRepository.save(profile));
    }

    public List<UserProfileResponse> searchUsers(String email) {
        return userProfileRepository.findByEmailContaining(email)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserProfile createDefaultProfile(String email) {
        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setName(email.split("@")[0]);
        profile.setRole("MEMBER");
        return userProfileRepository.save(profile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getRole()
        );
    }
}