package com.xinra.listaide.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ListaideUser {
	
	@Id
	private String id;
	
	private String accessToken;
	
	private String refreshToken;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	
	private String playlistsEtag;
	
	protected ListaideUser() {}
	
	public ListaideUser(String id, String accessToken, String refreshToken) {
		this.id = id;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		lastLogin = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPlaylistsEtag() {
		return playlistsEtag;
	}

	public void setPlaylistsEtag(String playlistsEtag) {
		this.playlistsEtag = playlistsEtag;
	}

}
