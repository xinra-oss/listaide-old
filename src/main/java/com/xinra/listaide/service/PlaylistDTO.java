package com.xinra.listaide.service;

import java.util.List;
import java.util.Set;

public interface PlaylistDTO extends IdentifiableSpotifyDTO {
	
	static final String Collaborative = "collaborative";
	static final String Description = "description";
	static final String Followers = "followers";
	static final String ImageUrl = "imageUrl";
	static final String Name = "name";
	static final String Owner = "owner";
	static final String PublicAccess = "publicAccess";
	static final String Tracks = "tracks";
	static final String Parents = "parents";
	static final String Children = "children";
	
	Boolean getCollaborative();
	void setCollaborative(Boolean collaborative);
	
	String getDescription();
	void setDescription(String description);
	
	Integer getFollowers();
	void setFollowers(Integer followers);
	
	String getImageUrl();
	void setImageUrl(String imageUrl);
	
	String getName();
	void setName(String name);
	
	UserDTO getOwner();
	void setOwner(UserDTO owner);
	
	Boolean getPublicAccess();
	void setPublicAccess(Boolean publicAccess);
	
	List<TrackDTO> getTracks();
	void setTracks(List<TrackDTO> tracks);
	
	Set<PlaylistDTO> getParents();
	void setParents(Set<PlaylistDTO> parents);
	
	Set<PlaylistDTO> getChildren();
	void setChildren(Set<PlaylistDTO> children);

}
