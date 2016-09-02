package com.xinra.listaide.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends CrudRepository<SpotifyPlaylist, String> {

	@Query("SELECT DISTINCT p FROM SpotifyPlaylist p LEFT JOIN FETCH p.parentIds WHERE p.user.id = :userId")
	List<SpotifyPlaylist> findByUserId(@Param("userId") String userId);
	
	@Query("SELECT DISTINCT p FROM SpotifyPlaylist p LEFT JOIN FETCH p.parentIds LEFT JOIN FETCH p.tracks t LEFT JOIN FETCH t.artists WHERE p.user.id = :userId")
	List<SpotifyPlaylist> findByUserIdEagerly(@Param("userId") String userId);
	
}
