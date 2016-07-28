package com.xinra.listaide.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Session {

	@Id
	private String sessionId;
	
	@NotNull
	private String accessToken;
	
	@NotNull
	private String refreshToken;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;
	
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
}
