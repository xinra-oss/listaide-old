package com.xinra.listaide.service;

public interface UserDTO extends IdentifiableSpotifyDTO {

	static final String ImageUrl = "imageUrl";
	
	String getImageUrl();
	void setImageUrl(String imageUrl);
	
}
