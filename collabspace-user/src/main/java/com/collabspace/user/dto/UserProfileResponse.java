package com.collabspace.user.dto;

public class UserProfileResponse {
	private Long id;
	private String email;
	private String name;
	private String avatarUrl;
	private String bio;
	private String role;
	
	public UserProfileResponse(Long id, String email, String name, String avatarUrl, String bio,
			   String role){
		this.id= id;
		this.email = email;
		this.name = name;
		this.avatarUrl = avatarUrl;
		this.bio = bio;
		this.role= role;
		
	}
	  public Long getId() { return id; }
	    public String getEmail() { return email; }
	    public String getName() { return name; }
	    public String getAvatarUrl() { return avatarUrl; }
	    public String getBio() { return bio; }
	    public String getRole() { return role; }

}
