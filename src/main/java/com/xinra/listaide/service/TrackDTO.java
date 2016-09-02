package com.xinra.listaide.service;

import java.util.Date;
import java.util.List;

public interface TrackDTO extends DTO {
	
	static final String Album = "album";
	static final String Artists = "artists";
	static final String Duration = "duration";
	static final String Name = "name";
	static final String AddedAt = "addedAt";
	static final String AddedBy = "addedBy";
	static final String Number = "number";
	
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

}
