package com.xinra.listaide.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class SpotifyPlaylist extends IdentifiableSpotifyEntity {
	
	private String userId;
	private String etag;
	private boolean collaborative;
	private String description;
	private int followers;
	private String imageUrl;
	private String name;
	private SpotifyUser owner;
	private boolean publicAccess;
	private String tracksEtag;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	private List<SpotifyTrack> tracks;
	
	@ManyToMany(mappedBy="children", cascade=CascadeType.ALL)
	private List<SpotifyPlaylist> parents;
	
	@ManyToMany(cascade=CascadeType.ALL)
	private List<SpotifyPlaylist> children;

	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
	public boolean isCollaborative() {
		return collaborative;
	}
	public void setCollaborative(boolean collaborative) {
		this.collaborative = collaborative;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SpotifyUser getOwner() {
		return owner;
	}
	public void setOwner(SpotifyUser owner) {
		this.owner = owner;
	}
	public boolean isPublicAccess() {
		return publicAccess;
	}
	public void setPublicAccess(boolean publicAccess) {
		this.publicAccess = publicAccess;
	}
	public List<SpotifyTrack> getTracks() {
		return tracks;
	}
	public void setTracks(List<SpotifyTrack> tracks) {
		this.tracks = tracks;
	}
	public String getTracksEtag() {
		return tracksEtag;
	}
	public void setTracksEtag(String tracksEtag) {
		this.tracksEtag = tracksEtag;
	}
	public int getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	public List<SpotifyPlaylist> getParents() {
		return parents;
	}
	public void setParents(List<SpotifyPlaylist> parents) {
		this.parents = parents;
	}
	public List<SpotifyPlaylist> getChildren() {
		return children;
	}
	public void setChildren(List<SpotifyPlaylist> children) {
		this.children = children;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
