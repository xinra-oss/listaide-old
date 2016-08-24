package com.xinra.listaide.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SpotifyEntity {
	
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
