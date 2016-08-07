package com.xinra.listaide.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Session {

	@Id
	private String sessionId;
	
	private String accessToken;
	
	private String refreshToken;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;
	
	private String playlistsEtag;
	
	protected Session() {}

	public Session(String sessionId, String accessToken, String refreshToken) {
		this.sessionId = sessionId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lastUsed = new Date();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	public String getPlaylistsEtag() {
		return playlistsEtag;
	}

	public void setPlaylistsEtag(String playlistsEtag) {
		this.playlistsEtag = playlistsEtag;
	}
}
