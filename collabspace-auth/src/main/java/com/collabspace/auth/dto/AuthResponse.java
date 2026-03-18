package com.collabspace.auth.dto;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String name;
    private String role;

    public AuthResponse(String accessToken, String email, String name, String role) {
        this.accessToken = accessToken;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
}