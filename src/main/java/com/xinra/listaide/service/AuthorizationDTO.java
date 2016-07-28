package com.xinra.listaide.service;

/**
 * @see AuthorizationService#beginAuthorization(String)
 * 
 * @author erikhofer
 */
public interface AuthorizationDTO extends DTO {
	
	public static final String Url = "url";
	public static final String SessionId = "sessionId";
	
	String getUrl();
	void setUrl(String url);
	
	String getSessionId();
	void setSessionId(String sessionId);
	
}
