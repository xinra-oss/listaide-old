package com.xinra.listaide.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class SpotifyPlaylist extends IdentifiableSpotifyEntity {
	
	@ManyToOne
	private ListaideUser user;
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
	private Set<SpotifyTrack> tracks;
	
	@ManyToMany(mappedBy="children", cascade=CascadeType.ALL)
	private Set<SpotifyPlaylist> parents;
	
	@ManyToMany(cascade=CascadeType.ALL)
	private Set<SpotifyPlaylist> children;
	
	public ListaideUser getUser() {
		return user;
	}
	
	public void setUser(ListaideUser user) {
		this.user = user;
	}

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
	public Set<SpotifyTrack> getTracks() {
		return tracks;
	}
	public void setTracks(Set<SpotifyTrack> tracks) {
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
	public Set<SpotifyPlaylist> getParents() {
		return parents;
	}
	public void setParents(Set<SpotifyPlaylist> parents) {
		this.parents = parents;
	}
	public Set<SpotifyPlaylist> getChildren() {
		return children;
	}
	public void setChildren(Set<SpotifyPlaylist> children) {
		this.children = children;
	}

}
