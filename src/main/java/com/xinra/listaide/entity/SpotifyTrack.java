package com.xinra.listaide.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class SpotifyTrack extends SpotifyEntity {
	
	//needed because the same track can exist multiple times
	//for faster read operations
	@Id
	@GeneratedValue
	private long id;
	
	//actually these 3 are kind of a composite key
	//in terms of business logic
	private String trackId;
	private Date addedAt;
	private SpotifyUser addedBy;
	
	private SpotifyAlbum album;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<SpotifyArtist> artists;
	private int duration;
	private String name;
	private int number;
	
	public SpotifyAlbum getAlbum() {
		return album;
	}
	public void setAlbum(SpotifyAlbum album) {
		this.album = album;
	}
	public Set<SpotifyArtist> getArtists() {
		return artists;
	}
	public void setArtists(Set<SpotifyArtist> artists) {
		this.artists = artists;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getAddedAt() {
		return addedAt;
	}
	public void setAddedAt(Date addedAt) {
		this.addedAt = addedAt;
	}
	public SpotifyUser getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(SpotifyUser addedBy) {
		this.addedBy = addedBy;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
}
