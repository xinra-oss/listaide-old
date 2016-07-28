package com.xinra.listaide.service;

/**
 * This service manages all Spotify entities (e.g. playlists) by fetching the Sprotify
 * Web API and caching the results.
 * 
 * @author erikhofer
 */
public interface SpotifyService extends Service {

	/**
	 * Attaches a session to the service. This should be done at initialization as the service
	 * cannot access the Spotify Web API without an authorized session.
	 * @param sessionId the id of the session to attach
	 * @throws ServiceException
	 */
	public UserDTO attachSession(String sessionId) throws ServiceException;
	
}
