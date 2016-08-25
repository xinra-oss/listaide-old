package com.xinra.listaide.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Session {

	@Id
	private String sessionId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private ListaideUser user;
	
	protected Session() {}

	public Session(String sessionId, ListaideUser user) {
		this.sessionId = sessionId;
		this.user = user;
		this.lastUsed = new Date();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	public ListaideUser getUser() {
		return user;
	}

	public void setUser(ListaideUser user) {
		this.user = user;
	}
}
