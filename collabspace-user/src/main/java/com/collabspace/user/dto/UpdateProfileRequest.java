package com.collabspace.user.dto;

public class UpdateProfileRequest {
    private String name;
    private String bio;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}