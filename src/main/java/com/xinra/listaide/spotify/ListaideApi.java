package com.xinra.listaide.spotify;

import com.wrapper.spotify.Api;

/**
 * Provides additional functionality that is not yet available in the Spotify API wrapper.
 * This has to be used in addition to the original API which cannot be extended.
 * 
 * @author erikhofer
 */
public class ListaideApi {
	
	protected final Api wrapped;

	public ListaideApi(Api wrapped) {
		this.wrapped = wrapped;
	}
	
}
