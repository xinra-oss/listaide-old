package com.xinra.listaide.entity;

import javax.persistence.Embeddable;

@Embeddable
public class SpotifyUser extends IdentifiableSpotifyEmbeddable {
	
	private String imageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
