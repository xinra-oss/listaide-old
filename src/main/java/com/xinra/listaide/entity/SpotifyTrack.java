package com.xinra.listaide.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class SpotifyTrack extends IdentifiableSpotifyEntity {
	
	private SpotifyAlbum album;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	private List<SpotifyArtist> artists;
	private int duration;
	private String name;
	private Date addedAt;
	private SpotifyUser addedBy;
	private int number;
	
	public SpotifyAlbum getAlbum() {
		return album;
	}
	public void setAlbum(SpotifyAlbum album) {
		this.album = album;
	}
	public List<SpotifyArtist> getArtists() {
		return artists;
	}
	public void setArtists(List<SpotifyArtist> artists) {
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
}
