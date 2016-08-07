package com.xinra.listaide.entity;

import javax.persistence.Embeddable;

@Embeddable
public class SpotifyAlbum extends SpotifyEntity {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
