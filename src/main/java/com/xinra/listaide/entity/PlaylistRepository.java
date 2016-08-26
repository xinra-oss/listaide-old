package com.xinra.listaide.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends CrudRepository<SpotifyPlaylist, String> {

	List<SpotifyPlaylist> findByUserId(String userId);
	
	@Query("SELECT DISTINCT p FROM SpotifyPlaylist p INNER JOIN FETCH p.tracks t INNER JOIN FETCH t.artists WHERE p.user.id = :userId")
	List<SpotifyPlaylist> findByUserIdEagerly(@Param("userId") String userId);
	
}
