package com.xinra.listaide.service;


/**
 * Service for obtaining Spotify authorization.
 * 
 * @author erikhofer
 */
public interface AuthorizationService extends Service {

	/**
	 * Begin the Authorization Code Grant flow. Generates the URL for the link the user has to click
	 * on to connect with Spotify. Generates the User's session id which is later used
	 * to complete the authorization.
	 * @param redirectUri the URI to which the user is redirected after authorization. This has to
	 * be set up at https://developer.spotify.com/my-applications/
	 * @return a DTO containing the authorization URL and the user's session id
	 */
	AuthorizationDTO beginAuthorization(String redirectUri);
	
	/**
	 * Second step of the Authorization Code Grant flow.
	 * @param sessionId
	 * @param state
	 * @param code
	 */
	void completeAuthorization(String sessionId, String state, String code, String redirectUri) throws ServiceException;
	
}
