package com.collabspace.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.collabspace.user.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	Optional<UserProfile> findByEmail(String email);
	List<UserProfile> findByEmailContaining(String email);
}
