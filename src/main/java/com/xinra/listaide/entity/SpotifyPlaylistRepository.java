package com.xinra.listaide.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SpotifyPlaylistRepository extends CrudRepository<SpotifyPlaylist, String> {

	List<SpotifyPlaylist> findByUserId(String userId);
	
}
