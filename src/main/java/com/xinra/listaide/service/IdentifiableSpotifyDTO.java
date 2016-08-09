package com.xinra.listaide.service;

public interface IdentifiableSpotifyDTO extends SpotifyDTO {
	
	static final String Id = "id";
	
	String getId();
	void setId(String id);

}
