package com.xinra.listaide.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TrackDTO extends DTO {
	
	static final String Id = "id";
	static final String TrackId = "trackId";
	static final String Album = "album";
	static final String Artists = "artists";
	static final String Duration = "duration";
	static final String Name = "name";
	static final String AddedAt = "addedAt";
	static final String AddedBy = "addedBy";
	static final String Number = "number";
	static final String InheritedFrom = "inheritedFrom";
	static final String BequeathedTo = "bequeathedTo";
	
	Long getId();
	void setId(Long id);
	
	String getTrackId();
	void setTrackId(String trackId);
	
	AlbumDTO getAlbum();
	void setAlbum(AlbumDTO album);
	
	List<ArtistDTO> getArtists();
	void setArtists(List<ArtistDTO> artists);
	
	Integer getDuration();
	void setDuration(Integer duration);
	
	String getName();
	void setName(String name);
	
	Date getAddedAt();
	void setAddedAt(Date addedAt);
	
	UserDTO getAddedBy();
	void setAddedBy(UserDTO addedBy);
	
	Integer getNumber();
	void setNumber(Integer number);
	
	Map<PlaylistDTO, Set<TrackDTO>> getInheritedFrom();
	void setInheritedFrom(Map<PlaylistDTO, Set<TrackDTO>> inheritedFrom);
	
	Map<PlaylistDTO, Set<TrackDTO>> getBequeathedTo();
	void setBequeathedTo(Map<PlaylistDTO, Set<TrackDTO>> bequeathedTo);

}
