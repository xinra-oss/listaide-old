package com.xinra.listaide.service;

import java.util.List;

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
	 * @param login is the user actually logging in through the front end?
	 * @throws ServiceException
	 */
	UserDTO attachSession(String sessionId, Boolean login) throws ServiceException;
	
	/**
	 * Synchronize with the Spotify Web API. This will validate caches and fetch
	 * new data if applicable.
	 * 
	 * @throws ServiceException
	 */
	void synchronize() throws ServiceException;
	
	/**
	 * Returns playlists of a specific user. Tracks are not loaded and must
	 * be fetched on demand via {@link #getTracks(String)}.
	 * @param userId the id of the user whose playlists should be returned
	 */
	List<PlaylistDTO> getPlaylists(String userId);
	
	/**
	 * Returns the tracks of a specific playlist.
	 * @param playlistId the id of the playlist the tracks of which should be returned
	 */
	List<TrackDTO> getTracks(String playlistId);
	
}
